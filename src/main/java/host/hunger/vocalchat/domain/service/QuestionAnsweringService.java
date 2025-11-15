package host.hunger.vocalchat.domain.service;

import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface QuestionAnsweringService {
    String answerQuestion(QuestionRequest request, AIAssistant aiAssistant);

    CompletableFuture<String> answerQuestionAsync(QuestionRequest request, AIAssistant aiAssistant);

    void streamingAnswerQuestionAsync(QuestionRequest request,
                                      AIAssistant aiAssistant,
                                      Consumer<String> onToken,
                                      Consumer<String> onComplete,
                                      Consumer<Throwable> onError);
}
