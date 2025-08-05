package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
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

    public void answerQuestion(QuestionRequest request, String sessionId) {
        AIAssistant aiAssistant = aiAssistantRepository.findById();//todo
        questionAnsweringService.answerQuestion(request, aiAssistant, sessionId);
    }

}
