package host.hunger.vocalchat.domain.factory;

import host.hunger.vocalchat.domain.model.aiassistant.*;
import host.hunger.vocalchat.domain.model.user.UserId;
import org.springframework.stereotype.Component;

@Component
public class AIAssistantFactory {

    public static AIAssistant create(UserId userId, AIAssistantName name,
                                     AIAssistantDescription description,
                                     AIAssistantCharacter character) {
        return new AIAssistant(userId, name, description, character);
    }

    public static AIAssistant createWithGeneratedId(UserId userId, AIAssistantName name,
                                                    AIAssistantDescription description,
                                                    AIAssistantCharacter character) {
        AIAssistant assistant = create(userId, name, description, character);
        assistant.setId(AIAssistantId.generate(AIAssistantId.class));
        return assistant;
    }
}
