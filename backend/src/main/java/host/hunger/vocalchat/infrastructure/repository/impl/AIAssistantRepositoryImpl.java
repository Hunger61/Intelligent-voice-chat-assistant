package host.hunger.vocalchat.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import host.hunger.vocalchat.domain.factory.DialogueFactory;
import host.hunger.vocalchat.domain.model.aiassistant.*;
import host.hunger.vocalchat.domain.model.dialogue.*;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.infrastructure.repository.persistence.entity.AIAssistantDO;
import host.hunger.vocalchat.infrastructure.repository.persistence.entity.DialogueDO;
import host.hunger.vocalchat.infrastructure.repository.persistence.mapper.AIAssistantMapper;
import host.hunger.vocalchat.infrastructure.repository.persistence.mapper.DialogueMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AIAssistantRepositoryImpl implements AIAssistantRepository {

    private final AIAssistantMapper aiAssistantMapper;
    private final DialogueMapper dialogueMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Optional<AIAssistant> findById(AIAssistantId aiAssistantId) {
        return Optional.ofNullable(toDomain(aiAssistantMapper.selectById(aiAssistantId.toString())));
    }

    @Override
    public List<AIAssistant> findAll() {
        return toDomain(aiAssistantMapper.selectList(new QueryWrapper<>()));
    }

    @Override
    public List<AIAssistant> findByUserId(UserId userId) {
        return toDomain(aiAssistantMapper.selectList(new LambdaQueryWrapper<AIAssistantDO>().eq(AIAssistantDO::getUserId, userId.toString())));
    }

    @Override
    public void delete(AIAssistantId aiAssistantId) {
        aiAssistantMapper.deleteById(aiAssistantId.toString());
        deleteDialogue(aiAssistantId);
    }

    @Override
    public void save(AIAssistant aiAssistant) {
        AIAssistantDO aiAssistantDO = toDataObject(aiAssistant);
        if (exists(aiAssistant.getId())){
            aiAssistantMapper.updateById(aiAssistantDO);
        }else{
            aiAssistantMapper.insert(aiAssistantDO);
        }
    }

    @Override
    public boolean exists(AIAssistantId aiAssistantId) {
        return aiAssistantMapper.selectById(aiAssistantId.toString()) != null;
    }

    private AIAssistant toDomain(AIAssistantDO assistantDO) {
        if (assistantDO== null){
            return null;
        }
        KnowledgeBaseId knowledgeBaseId = null;
        if (assistantDO.getKnowledgeBaseId() != null && !assistantDO.getKnowledgeBaseId().trim().isEmpty()) {
            knowledgeBaseId = new KnowledgeBaseId(assistantDO.getKnowledgeBaseId());
        }// todo 改成域内校验合法性
        return new AIAssistant(
                new AIAssistantId(assistantDO.getId()),
                new UserId(assistantDO.getUserId()),
                new AIAssistantName(assistantDO.getName()),
                new AIAssistantDescription(assistantDO.getDescription()),
                new AIAssistantCharacter(assistantDO.getAssistantCharacter()),
                knowledgeBaseId
        );
    }

    private AIAssistantDO toDataObject(AIAssistant assistant) {
        if (assistant == null){
            return null;
        }
        AIAssistantDO assistantDO = new AIAssistantDO();
        if (assistant.getId() != null){
            assistantDO.setId(assistant.getId().toString());
        }
        assistantDO.setUserId(assistant.getUserId().toString());
        assistantDO.setName(assistant.getName().getName());
        assistantDO.setDescription(assistant.getDescription().getDescription());
        assistantDO.setAssistantCharacter(assistant.getAssistantCharacter().getCharacter());
        if (assistant.getKnowledgeBaseId() != null){
            assistantDO.setKnowledgeBaseId(assistant.getKnowledgeBaseId().toString());
        }
        return assistantDO;
    }

    private List<AIAssistant> toDomain(List<AIAssistantDO> assistantDOs) {
        return assistantDOs.stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Dialogue> findDialogueByAIAssistantId(AIAssistantId aiAssistantId) {
        DialogueDO dialogueDO = dialogueMapper.selectOne(
                new LambdaQueryWrapper<DialogueDO>().eq(DialogueDO::getAiAssistantId, aiAssistantId.toString()));
        return Optional.ofNullable(toDialogueDomain(dialogueDO));
    }

    @Override
    public void saveDialogue(Dialogue dialogue) {
        DialogueDO dialogueDO = toDialogueDataObject(dialogue);
        if (dialogueMapper.selectById(dialogueDO.getId()) == null) {
            dialogueMapper.insert(dialogueDO);
        } else {
            dialogueMapper.updateById(dialogueDO);
        }
    }

    @Override
    public void deleteDialogue(AIAssistantId aiAssistantId) {
        dialogueMapper.delete(new LambdaQueryWrapper<DialogueDO>()
                .eq(DialogueDO::getAiAssistantId, aiAssistantId.toString()));
    }

    private Dialogue toDialogueDomain(DialogueDO d) {
        if (d == null) {
            return null;
        }
        AIAssistantId aiId = new AIAssistantId(d.getAiAssistantId());
        DialogueId dialogueId = new DialogueId(d.getId());
        ArrayList<DialogueContext> contexts = new ArrayList<>();
        String ctxJson = d.getContexts();
        if (ctxJson != null && !ctxJson.trim().isEmpty()) {
            try {
                List<Map<String, String>> list = objectMapper.readValue(ctxJson, new TypeReference<>() {});
                for (Map<String, String> item : list) {
                    String roleStr = item.get("role");
                    String contentStr = item.get("content");
                    DialogueRole role = DialogueRole.from(roleStr);
                    DialogueContent content = new DialogueContent(contentStr);
                    DialogueContext context = new DialogueContext(role, content);
                    contexts.add(context);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse dialogue contexts JSON", e);
            }
        }
        return DialogueFactory.reconstitute(dialogueId, aiId, contexts);
    }

    private DialogueDO toDialogueDataObject(Dialogue dialogue) {
        if (dialogue == null) {
            return null;
        }
        DialogueDO d = new DialogueDO();
        d.setId(dialogue.getId() == null ? null : dialogue.getId().toString());
        d.setAiAssistantId(dialogue.getAiAssistantId().toString());
        List<Map<String, String>> list = new ArrayList<>();
        for (DialogueContext ctx : dialogue.getDialogueContexts()) {
            Map<String, String> m = new HashMap<>();
            m.put("role", ctx.getRole().getRole().name());
            m.put("content", ctx.getContent().getContent());
            list.add(m);
        }
        try {
            d.setContexts(objectMapper.writeValueAsString(list));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize dialogue contexts", e);
        }
        return d;
    }
}
