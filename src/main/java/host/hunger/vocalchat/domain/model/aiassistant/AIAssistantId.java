package host.hunger.vocalchat.domain.model.aiassistant;

import host.hunger.vocalchat.domain.model.shared.Identity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class AIAssistantId extends Identity {
    public AIAssistantId(String id) {
            super(id);
    }
}
