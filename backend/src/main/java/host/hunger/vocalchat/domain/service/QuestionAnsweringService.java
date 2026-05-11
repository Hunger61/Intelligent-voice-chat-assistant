package host.hunger.vocalchat.domain.service;

import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;

import java.util.function.Consumer;

public interface QuestionAnsweringService {
    void streamingAnswerQuestionAsync(QuestionRequest request,
                                      AIAssistant aiAssistant,
                                      Consumer<String> onToken,
                                      Consumer<String> onThinking,
                                      Consumer<String> onComplete,
                                      Consumer<Throwable> onError);
}
