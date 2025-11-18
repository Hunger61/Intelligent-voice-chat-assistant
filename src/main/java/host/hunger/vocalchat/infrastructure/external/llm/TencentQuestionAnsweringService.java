package host.hunger.vocalchat.infrastructure.external.llm;

import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TencentQuestionAnsweringService implements QuestionAnsweringService {
    @Override
    public  String answerQuestion(QuestionRequest request, AIAssistant aiAssistant) {
        return null;
    }

    @Override
    public CompletableFuture<String> answerQuestionAsync(QuestionRequest request, AIAssistant aiAssistant) {
        return null;
    }

    @Override
    public void streamingAnswerQuestionAsync(QuestionRequest request, AIAssistant aiAssistant, Consumer<String> onToken, Consumer<String> onComplete, Consumer<Throwable> onError) {

    }
}
