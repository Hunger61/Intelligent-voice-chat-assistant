package host.hunger.vocalchat.infrastructure.external.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.event.QuestionAnsweredEvent;
import host.hunger.vocalchat.domain.event.DomainEventPublisher;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OllamaQuestionAnsweringService implements QuestionAnsweringService {

    private final DomainEventPublisher domainEventPublisher;

    private final ChatLanguageModel chatLanguageModel = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("deepseek-r1:14b")
            .build();

    @Override
    public void answerQuestion(QuestionRequest request, AIAssistant aiAssistant) {
        try {
            QuestionAnsweredEvent questionAnsweredEvent = new QuestionAnsweredEvent(
                aiAssistant.getId(),
                request.getQuestion(),
                chatLanguageModel.chat(request.getQuestion()),
                aiAssistant.getUserId()
        );
            domainEventPublisher.publish(questionAnsweredEvent);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }
}
