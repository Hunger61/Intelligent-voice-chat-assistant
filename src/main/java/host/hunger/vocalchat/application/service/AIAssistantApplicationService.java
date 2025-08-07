package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIAssistantApplicationService {

    private final AIAssistantRepository aiAssistantRepository;

    public AIAssistant findAIAssistantById(String id) {
        if(id==null||id.trim().isBlank()){
            throw new IllegalArgumentException("AI Assistant ID cannot be null or empty");
        }
        AIAssistantId aiAssistantId = new AIAssistantId(id);
        if(!aiAssistantRepository.exists(aiAssistantId)){
            throw new IllegalArgumentException("AI Assistant does not exist");
        }
        return aiAssistantRepository.findById(aiAssistantId);
    }
}
