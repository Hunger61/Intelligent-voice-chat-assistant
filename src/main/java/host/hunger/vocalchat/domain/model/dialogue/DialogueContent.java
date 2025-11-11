package host.hunger.vocalchat.domain.model.dialogue;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.Getter;

@Getter
public class DialogueContent extends ValueObject {
    private final String content;
    public DialogueContent(String content){
        if (content == null || content.trim().isEmpty()){
            throw new IllegalArgumentException("Invalid content");
        }
        this.content = content;
    }
}
