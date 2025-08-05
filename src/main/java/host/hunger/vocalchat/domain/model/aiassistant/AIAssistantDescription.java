package host.hunger.vocalchat.domain.model.aiassistant;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class AIAssistantDescription extends ValueObject {
    private final String description;

    public AIAssistantDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty.");
        }
        if (description.length() > 200) {
            throw new IllegalArgumentException("Description cannot be longer than 200 characters.");
        }
        this.description = description;
    }
}
