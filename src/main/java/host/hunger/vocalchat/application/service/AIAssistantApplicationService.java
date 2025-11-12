package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.dialogue.Dialogue;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContent;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContext;
import host.hunger.vocalchat.domain.model.dialogue.DialogueRole;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.domain.repository.DialogueRepository;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;
import host.hunger.vocalchat.infrastructure.Enum.DialogueRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AIAssistantApplicationService {

    private final AIAssistantRepository aiAssistantRepository;
    private final QuestionAnsweringService questionAnsweringService;
    private final DialogueRepository dialogueRepository;

    public AIAssistant findAIAssistantById(AIAssistantId aiAssistantId) {
        if (aiAssistantId == null) {
            throw new IllegalArgumentException("AI Assistant ID cannot be null");
        }
        AIAssistant aiAssistant = aiAssistantRepository.findById(aiAssistantId);
        if (aiAssistant == null) {
            throw new IllegalArgumentException("AI Assistant does not exist");
        }
        return aiAssistant;
    }

    //todo 可能的异步操作
    @Transactional
    public String answerQuestion(String question, String aiAssistantId) {
        AIAssistantId assistantId = new AIAssistantId(aiAssistantId);
        AIAssistant aiAssistant = findAIAssistantById(assistantId);
        Dialogue byAIAssistantId = dialogueRepository.findByAIAssistantId(assistantId);
        byAIAssistantId.addContext(new DialogueContext(new DialogueRole(DialogueRoles.USER), new DialogueContent(question)));
        QuestionRequest request = new QuestionRequest(byAIAssistantId.getDialogueContexts(), false);
        String answer = questionAnsweringService.answerQuestion(request, aiAssistant);
        byAIAssistantId.addContext(new DialogueContext(new DialogueRole(DialogueRoles.ASSISTANT), new DialogueContent(answer)));
        dialogueRepository.save(byAIAssistantId);
        return answer;
    }
}
