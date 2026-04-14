package host.hunger.vocalchat.domain.model.aiassistant;

import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.model.shared.AggregateRoot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
//todo 用MybatisPlus实现UUID创建。
public class AIAssistant extends AggregateRoot<AIAssistantId> {

    private AIAssistantName name;
    private AIAssistantDescription description;
    private AIAssistantCharacter assistantCharacter;
    private UserId userId;


    public AIAssistant (AIAssistantName name,
                                     AIAssistantDescription description,
                                     AIAssistantCharacter character) {
        this.name = name;
        this.description = description;
        this.assistantCharacter = character;
    }

    public AIAssistant(UserId userId, AIAssistantName name,
                       AIAssistantDescription description, AIAssistantCharacter character) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.assistantCharacter = character;
    }

    public AIAssistant(AIAssistantId aiAssistantId, UserId userId, AIAssistantName name,
                       AIAssistantDescription description, AIAssistantCharacter character) {
        super(aiAssistantId);
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.assistantCharacter = character;
    }
}
