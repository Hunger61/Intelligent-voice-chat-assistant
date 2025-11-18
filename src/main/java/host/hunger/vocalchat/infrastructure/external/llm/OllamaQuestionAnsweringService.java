package host.hunger.vocalchat.infrastructure.external.llm;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContext;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;
import host.hunger.vocalchat.infrastructure.Enum.DialogueRoles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
@Slf4j
public class OllamaQuestionAnsweringService implements QuestionAnsweringService {

    private final ChatLanguageModel chatLanguageModel;
    private final StreamingChatLanguageModel streamingChatLanguageModel;
//    private final ChatMemory chatMemory;

    private final ExecutorService virtualExecutor;

    @Override
    public String answerQuestion(QuestionRequest request, AIAssistant aiAssistant) {
        Instant start = Instant.now();
        log.info("Start answerQuestion for assistant={}, messagesCount={}", aiAssistant.getId(), request.getMessages() == null ? 0 : request.getMessages().size());

        ConversationalChain chain = initializeAIModelWithMemory(request.getMessages(), aiAssistant, chatLanguageModel);

        Instant beforeCall = Instant.now();
        String userInput = extractLatestUserMessage(request);
        if (userInput == null || userInput.isBlank()) {
            log.error("User input is empty when calling LLM");
            throw new IllegalArgumentException("User input cannot be empty");
        }
        String aiMessage = chain.execute(userInput);// pass actual user message

        Instant afterCall = Instant.now();

        if (aiMessage.isBlank()) {
            log.error("LLM returned null AiMessage for assistant={}", aiAssistant.getId());
            throw new RuntimeException("LLM returned null response");
        }

        log.info("LLM answered in {} ms, total elapsed {} ms", Duration.between(beforeCall, afterCall).toMillis(), Duration.between(start, Instant.now()).toMillis());
        log.debug("LLM response text: {}", aiMessage);
        return aiMessage;
    }

    @Override
    public CompletableFuture<String> answerQuestionAsync(QuestionRequest request, AIAssistant aiAssistant) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return answerQuestion(request, aiAssistant);
            } catch (Exception e) {
                log.error("Async answer failed", e);
                throw new RuntimeException(e);
            }
        }, virtualExecutor);
    }

    private interface AIAssistantStream {
        TokenStream chat(String userMessage);
    }

    @Override
    public void streamingAnswerQuestionAsync(QuestionRequest request, AIAssistant aiAssistant,
                                             Consumer<String> onToken,
                                             Consumer<String> onComplete,
                                             Consumer<Throwable> onError) {
        log.info("streamingAnswerQuestionAsync start for assistant={}, msgCount={}", aiAssistant.getId(), request.getMessages() == null ? 0 : request.getMessages().size());
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(100);
        String system = aiAssistant.getDescription() == null ? "" : aiAssistant.getDescription().getDescription();
        chatMemory.add(createSystemMessage(system));
        log.debug("Added system message: {}", system);

        if (request.getMessages() != null) {
            for (DialogueContext dialogueContext : request.getMessages()) {
                log.debug("Adding message role={} content={}", dialogueContext.getRole(), dialogueContext.getContent());
                if (dialogueContext.getRole().getRole().equals(DialogueRoles.USER)) {
                    chatMemory.add(new UserMessage(dialogueContext.getContent().getContent()));
                } else if (dialogueContext.getRole().getRole().equals(DialogueRoles.ASSISTANT)) {
                    chatMemory.add(new AiMessage(dialogueContext.getContent().getContent()));
                } else {
                    log.warn("Invalid role in dialogueContext: {}", dialogueContext.getRole());
                }
            }
        }

        var ai = AiServices.builder(AIAssistantStream.class)
                .chatMemory(chatMemory)
                .streamingChatLanguageModel(streamingChatLanguageModel)
                .build();

        String userInput = extractLatestUserMessage(request);
        if (userInput == null || userInput.isBlank()) {
            IllegalArgumentException ex = new IllegalArgumentException("User input cannot be empty for streaming call");
            log.error("Streaming call aborted: {}", ex.getMessage());
            if (onError != null) onError.accept(ex);
            return;
        }

        try {
            log.info("Calling ai.chat for assistant={}, userInputLen={}", aiAssistant.getId(), userInput.length());
            TokenStream tokenStream = ai.chat(userInput);
            if (tokenStream == null) {
                log.error("Ai.chat returned null TokenStream");
                if (onError != null) onError.accept(new RuntimeException("TokenStream is null"));
                return;
            }
            log.info("TokenStream created: {}", tokenStream.getClass().getName());

            tokenStream.onPartialResponse(s -> {
                try {
                    log.debug("onPartialResponse invoked, tokenLen={}", s == null ? 0 : s.length());
                    if (onToken != null) onToken.accept(s == null ? "" : s);
                } catch (Throwable t) {
                    log.warn("onPartialResponse handler threw", t);
                }
            }).onError(e -> {
                try {
                    log.error("LLM stream error observed", e);
                    if (onError != null) onError.accept(e);
                } catch (Throwable t) {
                    log.error("onError handler threw", t);
                }
            }).onCompleteResponse(response -> {
                try {
                    String full = "";
                    if (response != null && response.aiMessage() != null && response.aiMessage().text() != null) {
                        full = response.aiMessage().text();
                    }
                    log.info("onCompleteResponse invoked, fullLen={}", full.length());
                    if (onComplete != null) onComplete.accept(full);
                } catch (Throwable t) {
                    log.error("onCompleteResponse handler threw", t);
                    if (onError != null) onError.accept(t);
                }
            });

            log.info("ai.chat invoked successfully and handlers registered");
        } catch (Exception e) {
            log.error("Failed to start token stream", e);
            if (onError != null) onError.accept(e);
        }
    }


    private SystemMessage createSystemMessage(String text) {
        return SystemMessage.from(
                "你" +
                        text);
    }
    //todo

    private ConversationalChain initializeAIModelWithMemory(List<DialogueContext> dialogueContexts, AIAssistant aiAssistant, ChatLanguageModel chatLanguageModel) {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(100);
        // add system
        String system = aiAssistant.getDescription() == null ? "" : aiAssistant.getDescription().getDescription();
        chatMemory.add(createSystemMessage(system));
        log.debug("Added system message: {}", system);

        if (dialogueContexts != null) {
            for (DialogueContext dialogueContext : dialogueContexts) {
                log.debug("Adding message role={} content={}", dialogueContext.getRole(), dialogueContext.getContent());
                if (dialogueContext.getRole().getRole().equals(DialogueRoles.USER)) {
                    chatMemory.add(new UserMessage(dialogueContext.getContent().getContent()));
                } else if (dialogueContext.getRole().getRole().equals(DialogueRoles.ASSISTANT)) {
                    chatMemory.add(new AiMessage(dialogueContext.getContent().getContent()));
                } else {
                    log.warn("Invalid role in dialogueContext: {}", dialogueContext.getRole());
                }
            }
        }
        return ConversationalChain.builder()
                .chatMemory(chatMemory)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }

    private String extractLatestUserMessage(QuestionRequest request) {
        if (request == null || request.getMessages() == null || request.getMessages().isEmpty()) return null;
        List<DialogueContext> msgs = request.getMessages();
        for (int i = msgs.size() - 1; i >= 0; i--) {
            DialogueContext ctx = msgs.get(i);
            if (ctx.getRole() != null && DialogueRoles.USER.equals(ctx.getRole().getRole())) {
                if (ctx.getContent() != null && ctx.getContent().getContent() != null) {
                    return ctx.getContent().getContent();
                }
            }
        }
        return null;
    }
}
