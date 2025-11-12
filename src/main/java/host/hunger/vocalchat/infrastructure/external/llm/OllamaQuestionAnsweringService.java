package host.hunger.vocalchat.infrastructure.external.llm;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContext;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;
import host.hunger.vocalchat.infrastructure.Enum.DialogueRoles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Service
@Slf4j
public class OllamaQuestionAnsweringService implements QuestionAnsweringService {

    private final ChatLanguageModel chatLanguageModel = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("deepseek-r1:14b")
            .build();


    private final ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public String answerQuestion(QuestionRequest request, AIAssistant aiAssistant) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(aiAssistant.getId().toString())
                .maxMessages(20)
                .build();
        chatMemory.add(createSystemMessage(aiAssistant.getDescription().getDescription()));
        for (DialogueContext dialogueContext : request.getMessages()) {
            if (dialogueContext.getRole().equals(DialogueRoles.USER)){
                chatMemory.add(new UserMessage(dialogueContext.getContent().getContent()));
            } else if (dialogueContext.getRole().equals(DialogueRoles.ASSISTANT)) {
                chatMemory.add(new AiMessage(dialogueContext.getContent().getContent()));
            }else {
                throw new IllegalArgumentException("Invalid role");
            }
        }
        AiMessage aiMessage = chatLanguageModel.chat(chatMemory.messages()).aiMessage();
        return aiMessage.text();
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

    private SystemMessage createSystemMessage(String text) {
        return SystemMessage.from(
                "你" +
                text);
    }
    //todo
}
