package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIAssistantApplicationService {

    private final AIAssistantRepository aiAssistantRepository;
    private final QuestionAnsweringService questionAnsweringService;

    public AIAssistant findAIAssistantById(AIAssistantId aiAssistantId) {
        if (aiAssistantId == null){
            throw new IllegalArgumentException("AI Assistant ID cannot be null");
        }
        AIAssistant aiAssistant = aiAssistantRepository.findById(aiAssistantId);
        if(aiAssistant == null){
            throw new IllegalArgumentException("AI Assistant does not exist");
        }
        return aiAssistant;
    }

    public void answerQuestion(AIAssistant aiAssistant,String question) {
        QuestionRequest request = new QuestionRequest(question, false);
        questionAnsweringService.answerQuestion(request,aiAssistant);
    }
}
