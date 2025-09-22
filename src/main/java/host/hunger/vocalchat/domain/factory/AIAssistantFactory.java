package host.hunger.vocalchat.domain.factory;

import host.hunger.vocalchat.domain.model.aiassistant.*;
import host.hunger.vocalchat.infrastructure.Enum.DefaultAIAssistants;
import org.springframework.stereotype.Component;

@Component
public class AIAssistantFactory {

    public static AIAssistant create(DefaultAIAssistants defaultAIAssistant) {
        return new AIAssistant(defaultAIAssistant.getName(), defaultAIAssistant.getDescription(), defaultAIAssistant.getCharacter());
    }

    public static AIAssistant create(AIAssistantName name,
                                     AIAssistantDescription description,
                                     AIAssistantCharacter character) {
        return new AIAssistant(name, description, character);
    }
}
