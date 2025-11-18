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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
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
        Dialogue byAIAssistantId = dialogueRepository.findByAIAssistantId(assistantId)//todo 对于dialogue考虑从redis等缓存中读取，减少数据库压力
                .orElseThrow(() -> new BaseException(ErrorEnum.DIALOGUE_NOT_FOUND));
        byAIAssistantId.addContext(new DialogueContext(new DialogueRole(DialogueRoles.USER), new DialogueContent(question)));
        QuestionRequest request = new QuestionRequest(byAIAssistantId.getDialogueContexts(), false);
        String answer = questionAnsweringService.answerQuestion(request, aiAssistant);
        byAIAssistantId.addContext(new DialogueContext(new DialogueRole(DialogueRoles.ASSISTANT), new DialogueContent(answer)));
        dialogueRepository.save(byAIAssistantId);
        return answer;
    }

    @Transactional
    public CompletableFuture<String> answerQuestionAsync(String question, String aiAssistantId) {
        AIAssistantId assistantId = new AIAssistantId(aiAssistantId);
        AIAssistant aiAssistant = getAIAssistantById(assistantId);
        Dialogue byAIAssistantId = dialogueRepository.findByAIAssistantId(assistantId)
                .orElseThrow(() -> new BaseException(ErrorEnum.DIALOGUE_NOT_FOUND));
        byAIAssistantId.addContext(new DialogueContext(new DialogueRole(DialogueRoles.USER), new DialogueContent(question)));
        QuestionRequest request = new QuestionRequest(byAIAssistantId.getDialogueContexts(), false);
        CompletableFuture<String> future = questionAnsweringService.answerQuestionAsync(request, aiAssistant);
        // when complete, persist the assistant reply
        future.thenAccept(answer -> {
            try {
                byAIAssistantId.addContext(new DialogueContext(new DialogueRole(DialogueRoles.ASSISTANT), new DialogueContent(answer)));
                dialogueRepository.save(byAIAssistantId);
            } catch (Exception e) {
                log.error("Failed to persist async answer", e);
            }
        }).exceptionally(ex -> {
            log.error("answerQuestionAsync failed", ex);
            return null;
        });
        return future;
    }

    public void streamingAnswerQuestionAsync(String question, String aiAssistantId, Consumer<String> onToken,
                                             Runnable onComplete, Consumer<Throwable> onError) {
        log.info("streamingAnswerQuestionAsync invoked: aiAssistantId={}, questionLen={}", aiAssistantId, question == null ? 0 : question.length());
        AIAssistantId assistantId = new AIAssistantId(aiAssistantId);
        AIAssistant aiAssistant = getAIAssistantById(assistantId);
        Dialogue dialogue = dialogueRepository.findByAIAssistantId(assistantId)
                .orElseThrow(() -> new BaseException(ErrorEnum.DIALOGUE_NOT_FOUND));

        // add user context locally and persist immediately so the downstream LLM service can read saved context if it fetches from DB
        dialogue.addContext(new DialogueContext(new DialogueRole(DialogueRoles.USER), new DialogueContent(question)));
        try {
            dialogueRepository.save(dialogue);
        } catch (Exception e) {
            log.warn("Failed to save user context before streaming; continuing", e);
        }

        QuestionRequest request = new QuestionRequest(dialogue.getDialogueContexts(), false);

        try {
            questionAnsweringService.streamingAnswerQuestionAsync(
                    request,
                    aiAssistant,
                    token -> {
                        try {
                            log.debug("stream token received length={}", token == null ? 0 : token.length());
                            if (onToken != null) onToken.accept(token);
                        } catch (Exception e) {
                            log.error("error in onToken handler", e);
                        }
                    },
                    full -> {
                        try {
                            // reload dialogue to avoid detached/merge issues in async thread
                            Dialogue fresh = dialogueRepository.findByAIAssistantId(assistantId).orElse(null);
                            if (fresh == null) {
                                log.warn("Dialogue disappeared when persisting full answer, creating new dialogue record");
                                fresh = DialogueFactory.createNewDialogue(assistantId);
                            }
                            fresh.addContext(new DialogueContext(new DialogueRole(DialogueRoles.ASSISTANT), new DialogueContent(full)));
                            dialogueRepository.save(fresh);
                            if (onComplete != null) onComplete.run();
                        } catch (Exception e) {
                            log.error("error while persisting full streaming answer", e);
                            if (onError != null) onError.accept(e);
                        }
                    },
                    ex -> {
                        log.error("streamingAnswerQuestionAsync encountered error", ex);
                        if (onError != null) onError.accept(ex);
                    }
            );
        } catch (Exception e) {
            log.error("Failed to start streamingAnswerQuestionAsync", e);
            if (onError != null) onError.accept(e);
        }
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
