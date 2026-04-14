package host.hunger.vocalchat.domain.event;

import host.hunger.vocalchat.domain.model.dialogue.DialogueContent;
import host.hunger.vocalchat.domain.model.dialogue.DialogueId;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.infrastructure.Enum.DialogueRoles;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MessageAddedEvent extends DomainEvent {
    private final DialogueId dialogueId;
    private final DialogueRoles role;
    private final DialogueContent content;

    public MessageAddedEvent(DialogueId dialogueId, DialogueRoles role, DialogueContent content) {
        super(UUID.randomUUID().toString(), LocalDateTime.now());
        this.dialogueId = dialogueId;
        this.role = role;
        this.content = content;
    }
}

