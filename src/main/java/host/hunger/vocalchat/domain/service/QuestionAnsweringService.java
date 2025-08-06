package host.hunger.vocalchat.domain.service;

import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;

public interface QuestionAnsweringService {
    void answerQuestion(QuestionRequest request, AIAssistant aiAssistant);
}
