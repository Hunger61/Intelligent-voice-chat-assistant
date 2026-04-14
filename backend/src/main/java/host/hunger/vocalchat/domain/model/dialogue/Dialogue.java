package host.hunger.vocalchat.domain.model.dialogue;

import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.shared.AggregateRoot;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.event.MessageAddedEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Dialogue extends AggregateRoot<DialogueId> {
    private final AIAssistantId aiAssistantId;
    private final ArrayList<DialogueContext> dialogueContexts = new ArrayList<>();

    public Dialogue(AIAssistantId aiAssistantId) {
        this.aiAssistantId = aiAssistantId;
    }


    public Dialogue(DialogueId dialogueId, AIAssistantId aiAssistantId) {
        super(dialogueId);
        this.aiAssistantId = aiAssistantId;
    }

    public void addContext(DialogueContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        dialogueContexts.add(context);
        publishDomainEvent(new MessageAddedEvent(this.getId(), context.getRole().getRole(), context.getContent()));
    }

    public List<DialogueContext> getDialogueContexts() {
        return Collections.unmodifiableList(dialogueContexts);
    }
}
