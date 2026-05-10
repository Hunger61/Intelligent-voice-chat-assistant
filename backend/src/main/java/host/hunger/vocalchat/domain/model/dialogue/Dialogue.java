package host.hunger.vocalchat.domain.model.dialogue;

import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.shared.Entity;
import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Dialogue extends Entity<DialogueId> {
    private final AIAssistantId aiAssistantId;
    private final ArrayList<DialogueContext> dialogueContexts = new ArrayList<>();

    public Dialogue(DialogueId dialogueId, AIAssistantId aiAssistantId) {
        super(dialogueId);
        this.aiAssistantId = aiAssistantId;
    }

    public Dialogue(DialogueId dialogueId, AIAssistantId aiAssistantId, ArrayList<DialogueContext> contexts) {
        super(dialogueId);
        this.aiAssistantId = aiAssistantId;
        if (contexts != null) {
            this.dialogueContexts.addAll(contexts);
        }
    }

    public void addContext(DialogueContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
        dialogueContexts.add(context);
    }

    public List<DialogueContext> getDialogueContexts() {
        return Collections.unmodifiableList(dialogueContexts);
    }
}
