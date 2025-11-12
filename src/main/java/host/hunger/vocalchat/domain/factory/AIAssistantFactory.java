package host.hunger.vocalchat.domain.factory;

import host.hunger.vocalchat.domain.model.aiassistant.*;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.infrastructure.Enum.DefaultAIAssistants;
import org.springframework.stereotype.Component;

@Component
public class AIAssistantFactory {

    public static AIAssistant createDeaultAIAssistant(DefaultAIAssistants defaultAIAssistant) {
        return new AIAssistant(defaultAIAssistant.getName(), defaultAIAssistant.getDescription(), defaultAIAssistant.getCharacter());
    }

    public static AIAssistant createNewAIAssistant(UserId userId, AIAssistantName name,
                                                   AIAssistantDescription description,
                                                   AIAssistantCharacter character) {
        return new AIAssistant(userId, name, description, character);
    }
}
