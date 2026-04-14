package host.hunger.vocalchat.domain.model.aiassistant;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class AIAssistantCharacter extends ValueObject {
    private final String character;

    public AIAssistantCharacter(String character) {
        if (character == null||character.trim().isEmpty()){
            throw new IllegalArgumentException("character cannot be null or empty");
        }
        if (character.length() > 100){
            throw new IllegalArgumentException("character cannot be longer than 100 characters");
        }
        this.character = character;
    }

}
