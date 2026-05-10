package host.hunger.vocalchat.domain.repository;

import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.model.dialogue.Dialogue;

import java.util.List;
import java.util.Optional;

public interface AIAssistantRepository extends Repository<AIAssistant, AIAssistantId>{
    List<AIAssistant> findByUserId(UserId userId);
    
    Optional<Dialogue> findDialogueByAIAssistantId(AIAssistantId aiAssistantId);
    
    void saveDialogue(Dialogue dialogue);
    
    void deleteDialogue(AIAssistantId aiAssistantId);
}
