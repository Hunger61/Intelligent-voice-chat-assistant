package host.hunger.vocalchat.infrastructure.external.llm;

import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.enums.DialogueRoles;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContext;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;

@Service
@Slf4j
public class TencentQuestionAnsweringService implements QuestionAnsweringService {

    private final QwenStreamingChatModel qwenStreamingChatModel;

    public TencentQuestionAnsweringService(@Qualifier("qwenStreamingChatModel") QwenStreamingChatModel qwenStreamingChatModel) {
        this.qwenStreamingChatModel = qwenStreamingChatModel;
    }

    @Override
    public String answerQuestion(QuestionRequest request, AIAssistant aiAssistant) {
        return null;
    }

    @Override
    public CompletableFuture<String> answerQuestionAsync(QuestionRequest request, AIAssistant aiAssistant) {
        return null;
    }

    @Override
    public void streamingAnswerQuestionAsync(QuestionRequest request, AIAssistant aiAssistant, Consumer<String> onToken,
            Consumer<String> onComplete, Consumer<Throwable> onError) {
        log.info("streamingAnswerQuestionAsync start for assistant={}, msgCount={}", aiAssistant.getId(),
                request.getMessages() == null ? 0 : request.getMessages().size());
        
        // 在单独的线程中执行流处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(100);
                String system = aiAssistant.getDescription() == null ? "" : aiAssistant.getDescription().getDescription();
                chatMemory.add(SystemMessage.from(system));
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
                
                log.info("Calling qwenStreamingChatModel.chat");
                qwenStreamingChatModel.chat(chatMemory.messages(), new StreamingChatResponseHandler() {
                    @Override
                    public void onPartialResponse(String onPartialResponse) {
                        try {
                            log.debug("onPartialResponse invoked, tokenLen={}",
                                    onPartialResponse == null ? 0 : onPartialResponse.length());
                            if (onToken != null)
                                onToken.accept(onPartialResponse == null ? "" : onPartialResponse);
                        } catch (Throwable t) {
                            log.warn("onPartialResponse handler threw", t);
                            if (onError != null) {
                                onError.accept(t);
                            }
                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse arg0) {
                        try {
                            String full = "";
                            if (arg0 != null && arg0.aiMessage() != null && arg0.aiMessage().text() != null) {
                                full = arg0.aiMessage().text();
                            }
                            log.info("onCompleteResponse invoked, fullLen={}", full.length());
                            if (onComplete != null) {
                                onComplete.accept(full);
                            }
                        } catch (Throwable t) {
                            log.error("onCompleteResponse handler threw", t);
                            if (onError != null) {
                                onError.accept(t);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable arg0) {
                        try {
                            log.error("LLM stream error observed", arg0);
                            if (onError != null) {
                                onError.accept(arg0);
                            }
                        } catch (Throwable t) {
                            log.error("onError handler threw", t);
                        }
                    }
                });
                log.info("qwenStreamingChatModel.chat invoked successfully");
            } catch (Exception e) {
                log.error("Failed to start streaming", e);
                if (onError != null) {
                    onError.accept(e);
                }
            }
        });
    }
}
