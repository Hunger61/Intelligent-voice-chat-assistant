package host.hunger.vocalchat.infrastructure.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import host.hunger.vocalchat.domain.factory.DialogueFactory;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.dialogue.Dialogue;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContext;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContent;
import host.hunger.vocalchat.domain.model.dialogue.DialogueId;
import host.hunger.vocalchat.domain.model.dialogue.DialogueRole;
import host.hunger.vocalchat.domain.repository.DialogueRepository;
import host.hunger.vocalchat.infrastructure.repository.persistence.entity.DialogueDO;
import host.hunger.vocalchat.infrastructure.repository.persistence.mapper.DialogueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DialogueRepositoryImpl implements DialogueRepository {

    private final DialogueMapper dialogueMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Dialogue findById(DialogueId dialogueId) {
        DialogueDO dialogueDO = dialogueMapper.selectById(dialogueId.toString());
        if (dialogueDO == null) return null;
        return toDomain(dialogueDO);
    }

    @Override
    public List<Dialogue> findAll() {
        List<DialogueDO> dos = dialogueMapper.selectList(null);
        List<Dialogue> result = new ArrayList<>();
        for (DialogueDO d : dos) {
            result.add(toDomain(d));
        }
        return result;
    }

    @Override
    public Dialogue findByAIAssistantId(AIAssistantId aiAssistantId) {
        DialogueDO dialogueDO = dialogueMapper.selectOne(new LambdaQueryWrapper<DialogueDO>().eq(DialogueDO::getAiAssistantId, aiAssistantId.toString()));
        return toDomain(dialogueDO);
    }

    @Override
    public void delete(DialogueId dialogueId) {
        dialogueMapper.deleteById(dialogueId.toString());
    }

    @Override
    public void save(Dialogue entity) {
        DialogueDO dialogueDO = toDataObject(entity);
        if (entity.getId() == null) {
            dialogueMapper.insert(dialogueDO);
        } else {
            dialogueMapper.updateById(dialogueDO);
        }
    }

    @Override
    public boolean exists(DialogueId dialogueId) {
        Long count = dialogueMapper.selectCount(new LambdaQueryWrapper<DialogueDO>().eq(DialogueDO::getId, dialogueId.toString()));
        return count != null && count > 0L;
    }

    private Dialogue toDomain(DialogueDO d) {
        AIAssistantId aiId = new AIAssistantId(d.getAiAssistantId());
        DialogueId dialogueId = new DialogueId(d.getId());
        ArrayList<DialogueContext> contexts = new ArrayList<>();
        String ctxJson = d.getContexts();
        if (ctxJson != null && !ctxJson.trim().isEmpty()) {
            try {
                List<Map<String, String>> list = objectMapper.readValue(ctxJson, new TypeReference<>() {
                });
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

    private DialogueDO toDataObject(Dialogue dialogue) {
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
