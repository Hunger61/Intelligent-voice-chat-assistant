package host.hunger.vocalchat.domain.model.dialogue;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.Getter;

@Getter
public class DialogueContext extends ValueObject {
    private final DialogueRole role;
    private final DialogueContent content;

    public DialogueContext(DialogueRole role, DialogueContent content){
        if (role == null) {
            throw new IllegalArgumentException("role cannot be null");
        }
        if (content == null){
            throw new IllegalArgumentException("Invalid content");
        }
        this.role = role;
        this.content = content;
    }
}
