package host.hunger.vocalchat.domain.repository;

import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.dialogue.Dialogue;
import host.hunger.vocalchat.domain.model.dialogue.DialogueId;

import java.util.Optional;

public interface DialogueRepository extends Repository<Dialogue, DialogueId>{

    Optional<Dialogue> findByAIAssistantId(AIAssistantId aiAssistantId);
}
