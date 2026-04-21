package host.hunger.vocalchat.domain.factory;

import host.hunger.vocalchat.domain.model.aiassistant.*;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;
import host.hunger.vocalchat.domain.model.user.UserId;

import org.springframework.stereotype.Component;

@Component
public class AIAssistantFactory {

    public static AIAssistant createNewAIAssistant(UserId userId, AIAssistantName name,
                                                   AIAssistantDescription description,
                                                   AIAssistantCharacter character) {
        return new AIAssistant(userId, name, description, character);
    }

    public static AIAssistant createNewAIAssistant(UserId userId, AIAssistantName name,
                                                   AIAssistantDescription description,
                                                   AIAssistantCharacter character,
                                                   KnowledgeBaseId knowledgeBaseId) {
        return new AIAssistant(userId, name, description, character, knowledgeBaseId);
    }
}
