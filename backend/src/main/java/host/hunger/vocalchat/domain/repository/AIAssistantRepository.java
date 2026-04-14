package host.hunger.vocalchat.domain.repository;

import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.user.UserId;

import java.util.List;

public interface AIAssistantRepository extends Repository<AIAssistant, AIAssistantId>{
    List<AIAssistant> findByUserId(UserId userId);
}
