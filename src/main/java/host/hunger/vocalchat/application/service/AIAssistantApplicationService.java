package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.factory.AIAssistantFactory;
import host.hunger.vocalchat.domain.factory.DialogueFactory;
import host.hunger.vocalchat.domain.model.aiassistant.*;
import host.hunger.vocalchat.domain.model.dialogue.Dialogue;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContent;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContext;
import host.hunger.vocalchat.domain.model.dialogue.DialogueRole;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.domain.repository.DialogueRepository;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;
import host.hunger.vocalchat.infrastructure.Enum.DialogueRoles;
import host.hunger.vocalchat.infrastructure.interceptor.UserInterceptor;
import host.hunger.vocalchat.infrastructure.exception.BaseException;
import host.hunger.vocalchat.infrastructure.Enum.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AIAssistantApplicationService {

    private final AIAssistantRepository aiAssistantRepository;
    private final QuestionAnsweringService questionAnsweringService;
    private final DialogueRepository dialogueRepository;

    public AIAssistant getAIAssistantById(AIAssistantId aiAssistantId) {
        if (aiAssistantId == null) {
            throw new BaseException(ErrorEnum.AI_ASSISTANT_ID_NULL);
        }
        return aiAssistantRepository.findById(aiAssistantId).orElseThrow(() -> new BaseException(ErrorEnum.AI_ASSISTANT_NOT_FOUND));
    }

    //todo 可能的异步操作
    @Transactional
    public String answerQuestion(String question, String aiAssistantId) {
        AIAssistantId assistantId = new AIAssistantId(aiAssistantId);
        AIAssistant aiAssistant = getAIAssistantById(assistantId);
        Dialogue byAIAssistantId = dialogueRepository.findByAIAssistantId(assistantId);
        byAIAssistantId.addContext(new DialogueContext(new DialogueRole(DialogueRoles.USER), new DialogueContent(question)));
        QuestionRequest request = new QuestionRequest(byAIAssistantId.getDialogueContexts(), false);
        String answer = questionAnsweringService.answerQuestion(request, aiAssistant);
        byAIAssistantId.addContext(new DialogueContext(new DialogueRole(DialogueRoles.ASSISTANT), new DialogueContent(answer)));
        dialogueRepository.save(byAIAssistantId);
        return answer;
    }

    @Transactional
    public void createNewAssistant(String name, String description, String character) {
        AIAssistantName aiAssistantName = new AIAssistantName(name);
        AIAssistantDescription aiAssistantDescription = new AIAssistantDescription(description);
        AIAssistantCharacter aiAssistantCharacter = new AIAssistantCharacter(character);
        UserId userId = UserInterceptor.userHolder.get().getId();
        AIAssistant newAIAssistant = AIAssistantFactory.createNewAIAssistant(userId, aiAssistantName, aiAssistantDescription, aiAssistantCharacter);
        aiAssistantRepository.save(newAIAssistant);
        Dialogue dialogue = DialogueFactory.createNewDialogue(newAIAssistant.getId());
        dialogueRepository.save(dialogue);
    }
}
