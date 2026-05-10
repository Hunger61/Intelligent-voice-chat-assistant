package host.hunger.vocalchat.domain.event;

import host.hunger.vocalchat.domain.enums.DialogueRoles;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContent;
import host.hunger.vocalchat.domain.model.dialogue.DialogueId;
import lombok.Getter;

@Getter
public class MessageAddedEvent extends DomainEvent {
    private final DialogueId dialogueId;
    private final DialogueRoles role;
    private final DialogueContent content;

    public MessageAddedEvent(DialogueId dialogueId, DialogueRoles role, DialogueContent content) {
        super(dialogueId.toString() + "MessageAddedEvent");//todo
        this.dialogueId = dialogueId;
        this.role = role;
        this.content = content;
    }
}

