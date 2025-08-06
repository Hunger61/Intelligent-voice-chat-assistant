package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionAnsweringApplicationService {

    private final QuestionAnsweringService questionAnsweringService;
    private final AIAssistantRepository aiAssistantRepository;

    public void answerQuestion(AIAssistant aiAssistant, String question) {
        QuestionRequest request = new QuestionRequest(question, false);
        questionAnsweringService.answerQuestion(request, aiAssistant);
    }

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
