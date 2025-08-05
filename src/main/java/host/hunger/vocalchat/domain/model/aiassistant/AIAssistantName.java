package host.hunger.vocalchat.domain.model.aiassistant;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class AIAssistantName extends ValueObject {
    private final String name;

    public AIAssistantName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Name cannot be longer than 50 characters");
        }
        this.name = name;
    }
}
