package host.hunger.vocalchat.domain.factory;

import host.hunger.vocalchat.domain.model.aiassistant.*;
import host.hunger.vocalchat.domain.model.user.UserId;

public class AIAssistantFactory {

    public static AIAssistant create(UserId userId, AIAssistantName name,
                                     AIAssistantDescription description,
                                     AIAssistantCharacter character) {
        return new AIAssistant(userId, name, description, character);
    }

    public static AIAssistant createWithGeneratedId(UserId userId, AIAssistantName name,
                                                    AIAssistantDescription description,
                                                    AIAssistantCharacter character) {
        AIAssistant assistant = new AIAssistant(userId, name, description, character);
        assistant.setId(AIAssistantId.generate(AIAssistantId.class));
        return assistant;
    }

//    public static AIAssistant createFromCommand(CreateAIAssistantCommand command) {
//        return new AIAssistant(
//                command.getUserId(),
//                command.getName(),
//                command.getDescription(),
//                command.getCharacter()
//        );
//    }
}
