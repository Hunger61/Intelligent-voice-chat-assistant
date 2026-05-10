package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.enums.DialogueRoles;
import host.hunger.vocalchat.domain.dto.request.QuestionRequest;
import host.hunger.vocalchat.domain.factory.AIAssistantFactory;
import host.hunger.vocalchat.domain.factory.DialogueFactory;
import host.hunger.vocalchat.domain.model.aiassistant.*;
import host.hunger.vocalchat.domain.model.dialogue.Dialogue;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContent;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContext;
import host.hunger.vocalchat.domain.model.dialogue.DialogueRole;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.domain.service.QuestionAnsweringService;
import host.hunger.vocalchat.infrastructure.exception.BaseException;
import host.hunger.vocalchat.shared.context.UserContext;
import host.hunger.vocalchat.shared.enums.ErrorEnum;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@Slf4j
public class AIAssistantApplicationService {

    private final AIAssistantRepository aiAssistantRepository;
    private final QuestionAnsweringService questionAnsweringService;

    public AIAssistantApplicationService(AIAssistantRepository aiAssistantRepository,
            @Qualifier("tencentQuestionAnsweringService") QuestionAnsweringService questionAnsweringService) {
        this.aiAssistantRepository = aiAssistantRepository;
        this.questionAnsweringService = questionAnsweringService;
    }

    public AIAssistant getAIAssistantById(AIAssistantId aiAssistantId) {
        if (aiAssistantId == null) {
            throw new BaseException(ErrorEnum.AI_ASSISTANT_ID_NULL);
        }
        return aiAssistantRepository.findById(aiAssistantId)
                .orElseThrow(() -> new BaseException(ErrorEnum.AI_ASSISTANT_NOT_FOUND));
    }

    public void streamingAnswerQuestionAsync(String question, String aiAssistantId, Consumer<String> onToken,
            Runnable onComplete, Consumer<Throwable> onError) {
        log.info("streamingAnswerQuestionAsync invoked: aiAssistantId={}, questionLen={}", aiAssistantId,
                question == null ? 0 : question.length());

        CompletableFuture.runAsync(() -> {
            try {
                AIAssistantId assistantId = new AIAssistantId(aiAssistantId);
                AIAssistant aiAssistant = getAIAssistantById(assistantId);
                Dialogue dialogue = aiAssistantRepository.findDialogueByAIAssistantId(assistantId)
                        .orElseThrow(() -> new BaseException(ErrorEnum.DIALOGUE_NOT_FOUND));

                dialogue.addContext(new DialogueContext(new DialogueRole(DialogueRoles.USER), new DialogueContent(question)));
                try {
                    aiAssistantRepository.saveDialogue(dialogue);
                } catch (Exception e) {
                    log.warn("Failed to save user context before streaming; continuing", e);
                }

                QuestionRequest request = new QuestionRequest(dialogue.getDialogueContexts(), false);

                questionAnsweringService.streamingAnswerQuestionAsync(
                        request,
                        aiAssistant,
                        token -> {
                            try {
                                if (onToken != null)
                                    onToken.accept(token);
                            } catch (Exception e) {
                                log.error("error in onToken handler", e);
                            }
                        },
                        full -> {
                            try {
                                Dialogue fresh = aiAssistantRepository.findDialogueByAIAssistantId(assistantId).orElse(null);
                                if (fresh == null) {
                                    log.warn("Dialogue disappeared when persisting full answer, creating new dialogue record");
                                    fresh = DialogueFactory.createNewDialogue(assistantId);
                                }
                                fresh.addContext(new DialogueContext(new DialogueRole(DialogueRoles.ASSISTANT),
                                        new DialogueContent(full)));
                                aiAssistantRepository.saveDialogue(fresh);
                                if (onComplete != null)
                                    onComplete.run();
                            } catch (Exception e) {
                                log.error("error while persisting full streaming answer", e);
                                if (onError != null)
                                    onError.accept(e);
                            }
                        },
                        ex -> {
                            log.error("streamingAnswerQuestionAsync encountered error", ex);
                            if (onError != null)
                                onError.accept(ex);
                        });
            } catch (Exception e) {
                log.error("Failed to start streamingAnswerQuestionAsync", e);
                if (onError != null)
                    onError.accept(e);
            }
        });
    }

    @Transactional
    public void createNewAssistant(String name, String description, String character) {
        createNewAssistant(name, description, character, null);
    }

    @Transactional
    public void createNewAssistant(String name, String description, String character, String knowledgeBaseId) {
        AIAssistantName aiAssistantName = new AIAssistantName(name);
        AIAssistantDescription aiAssistantDescription = new AIAssistantDescription(description);
        AIAssistantCharacter aiAssistantCharacter = new AIAssistantCharacter(character);
        UserId userId = UserContext.require().getId();
        KnowledgeBaseId knowledgeBaseIdObj = null;
        if (knowledgeBaseId != null && !knowledgeBaseId.trim().isEmpty()) {
            knowledgeBaseIdObj = new KnowledgeBaseId(knowledgeBaseId);
        }
        AIAssistant newAIAssistant = AIAssistantFactory.createNewAIAssistant(userId, aiAssistantName,
                aiAssistantDescription, aiAssistantCharacter, knowledgeBaseIdObj);
        aiAssistantRepository.save(newAIAssistant);
        Dialogue dialogue = DialogueFactory.createNewDialogue(newAIAssistant.getId());
        aiAssistantRepository.saveDialogue(dialogue);
    }

    public List<Pair<String, String>> getConversationLog(String aiAssistantId) {
        AIAssistantId assistantId = new AIAssistantId(aiAssistantId);
        Dialogue dialogue = aiAssistantRepository.findDialogueByAIAssistantId(assistantId)
                .orElseThrow(() -> new BaseException(ErrorEnum.DIALOGUE_NOT_FOUND));
        return dialogue.getDialogueContexts().stream()
                .map(context -> Pair.of(context.getRole().getRole().name(), context.getContent().getContent()))
                .toList();
    }

    public List<AIAssistant> getAIAssistants(UserId userId) {
        List<AIAssistant> aiAssistants = aiAssistantRepository.findByUserId(userId);
        return aiAssistants;
    }

    @Transactional
    public void modifyAssistantConfig(String aiAssistantId, String name, String description, String character,
            String knowledgeBaseId) {
        AIAssistant aiAssistant = aiAssistantRepository.findById(new AIAssistantId(aiAssistantId))
                .orElseThrow(() -> new BaseException(ErrorEnum.AI_ASSISTANT_NOT_FOUND));
        aiAssistant.modifyConfig(new AIAssistantName(name), new AIAssistantDescription(description), new AIAssistantCharacter(character), new KnowledgeBaseId(knowledgeBaseId.trim()));
        aiAssistantRepository.save(aiAssistant);
    }

    @Transactional
    public void deleteAssistant(String aiAssistantId) {
        AIAssistantId assistantId = new AIAssistantId(aiAssistantId);
        aiAssistantRepository.delete(assistantId);
    }
}
