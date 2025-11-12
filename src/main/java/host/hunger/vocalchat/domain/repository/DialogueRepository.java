package host.hunger.vocalchat.domain.repository;

import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.dialogue.Dialogue;
import host.hunger.vocalchat.domain.model.dialogue.DialogueId;

public interface DialogueRepository extends Repository<Dialogue, DialogueId>{
    Dialogue findByAIAssistantId(AIAssistantId aiAssistantId);
}
