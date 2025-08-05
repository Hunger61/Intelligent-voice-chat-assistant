package host.hunger.vocalchat.infrastructure.external.llm;

import dev.langchain4j.community.model.dashscope.QwenLanguageModel;
import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.dto.response.AnswerResponse;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.aiassistant.QuestionAnsweredEvent;
import host.hunger.vocalchat.domain.model.shared.DomainEventPublisher;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalQuestionAnsweringService implements QuestionAnsweringService {

    private final DomainEventPublisher domainEventPublisher;

    private final QwenLanguageModel qwenLanguageModel = QwenLanguageModel
            .builder()
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
            .apiKey("sk-b98a19646ceb48a4a8e362d5613045e2")
            .modelName("qwq-32b-preview")
            .build();

    @Override
    public void answerQuestion(QuestionRequest request, AIAssistant aiAssistant,String sessionId) {
        QuestionAnsweredEvent questionAnsweredEvent = new QuestionAnsweredEvent(
                aiAssistant.getId(),
                request.getQuestion(),
                qwenLanguageModel.generate(request.getQuestion()).content(),
                aiAssistant.getUserId(),
                sessionId
        );
        domainEventPublisher.publish(questionAnsweredEvent);
//        return new AnswerResponse(qwenLanguageModel.generate(request.getQuestion()).content()) ;
    }
}
