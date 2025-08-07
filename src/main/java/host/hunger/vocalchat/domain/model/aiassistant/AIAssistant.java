package host.hunger.vocalchat.domain.model.aiassistant;

import com.baomidou.mybatisplus.annotation.TableName;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.model.shared.AggregateRoot;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@TableName("ai_assistant")
@NoArgsConstructor
//todo 用MybatisPlus实现UUID创建。
public class AIAssistant extends AggregateRoot<AIAssistantId> {

    private UserId userId;
    private AIAssistantName name;
    private AIAssistantDescription description;
    private AIAssistantCharacter assistantCharacter;

    public AIAssistant(UserId userId, AIAssistantName name,
                       AIAssistantDescription description, AIAssistantCharacter character) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.assistantCharacter = character;
    }

    public AIAssistant(AIAssistantId aiAssistantId, UserId userId, AIAssistantName name,
                       AIAssistantDescription description, AIAssistantCharacter character) {
        super.setId(aiAssistantId);
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.assistantCharacter = character;
    }
}
