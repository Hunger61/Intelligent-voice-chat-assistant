package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.event.QuestionReceivedEvent;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionAnsweringApplicationService {
    private AIAssistant aiAssistant;
    private final QuestionAnsweringService questionAnsweringService;
    private final AIAssistantRepository aiAssistantRepository;

    public void answerQuestion(String question) {
        QuestionRequest request = new QuestionRequest(question, false);
        questionAnsweringService.answerQuestion(request,aiAssistant);
    }

    public void setAIAssistant(AIAssistantId aiAssistantId){
        this.aiAssistant = aiAssistantRepository.findById(aiAssistantId);
    }
}
