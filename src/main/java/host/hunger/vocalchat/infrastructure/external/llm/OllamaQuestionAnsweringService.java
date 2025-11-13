package host.hunger.vocalchat.infrastructure.external.llm;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
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

@RequiredArgsConstructor
@Service
@Slf4j
public class OllamaQuestionAnsweringService implements QuestionAnsweringService {

    private final ChatLanguageModel chatLanguageModel;
    private final StreamingChatLanguageModel streamingChatLanguageModel;

    private final ExecutorService virtualExecutor;

    @Override
    public String answerQuestion(QuestionRequest request, AIAssistant aiAssistant) {
        Instant start = Instant.now();
        log.info("Start answerQuestion for assistant={}, messagesCount={}", aiAssistant.getId(), request.getMessages() == null ? 0 : request.getMessages().size());

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(aiAssistant.getId().toString())
                .maxMessages(20)
                .build();// todo 是否是单例，且是否需要依照文档中的持久化方式？
        // add system
        String system = aiAssistant.getDescription() == null ? "" : aiAssistant.getDescription().getDescription();
        chatMemory.add(createSystemMessage(system));
        log.debug("Added system message: {}", system);

        if (request.getMessages() != null) {
            for (DialogueContext dialogueContext : request.getMessages()) {
                log.debug("Adding message role={} content={}", dialogueContext.getRole(), dialogueContext.getContent());
                if (dialogueContext.getRole().getRole().equals(DialogueRoles.USER)){
                    chatMemory.add(new UserMessage(dialogueContext.getContent().getContent()));
                } else if (dialogueContext.getRole().getRole().equals(DialogueRoles.ASSISTANT)) {
                    chatMemory.add(new AiMessage(dialogueContext.getContent().getContent()));
                } else {
                    log.warn("Invalid role in dialogueContext: {}", dialogueContext.getRole());
                }
            }
        }

        Instant beforeCall = Instant.now();
        AiMessage aiMessage;
        try {
            aiMessage = chatLanguageModel.chat(chatMemory.messages()).aiMessage();
        } catch (Exception e) {
            log.error("LLM call failed", e);
            throw e;
        }
        Instant afterCall = Instant.now();

        if (aiMessage == null) {
            log.error("LLM returned null AiMessage for assistant={}", aiAssistant.getId());
            throw new RuntimeException("LLM returned null response");
        }

        String text = aiMessage.text();
        log.info("LLM answered in {} ms, total elapsed {} ms", Duration.between(beforeCall, afterCall).toMillis(), Duration.between(start, Instant.now()).toMillis());
        log.debug("LLM response text: {}", text);
        return text;
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

    public CompletableFuture<String> streamingAnswerQuestionAsync(QuestionRequest request, AIAssistant aiAssistant) {
//        return CompletableFuture.supplyAsync(() -> {
//            streamingChatLanguageModel.chat(request.getMessages(), new StreamingChatResponseHandler() {
//                @Override
//                public void onPartialResponse(String s) {
//
//                }
//
//                @Override
//                public void onCompleteResponse(ChatResponse chatResponse) {
//                    log.info("LLM answered: {}", chatResponse.aiMessage().text());
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//
//                }
//            });
//        }, virtualExecutor);
        return null;//todo
    }


    private List<String> dialogueContextsToList(List<DialogueContext> dialogueContexts) {
        return dialogueContexts.stream()
                .map(dialogueContext -> dialogueContext.getRole().getRole() + ": " + dialogueContext.getContent().getContent())
                .toList();
    }//todo

    private SystemMessage createSystemMessage(String text) {
        return SystemMessage.from(
                "你" +
                text);
    }
    //todo
}
