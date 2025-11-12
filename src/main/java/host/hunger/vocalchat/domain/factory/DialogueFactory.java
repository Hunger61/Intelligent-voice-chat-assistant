package host.hunger.vocalchat.domain.factory;

import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.dialogue.Dialogue;
import host.hunger.vocalchat.domain.model.dialogue.DialogueContext;
import host.hunger.vocalchat.domain.model.dialogue.DialogueId;
import host.hunger.vocalchat.domain.model.shared.Identity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DialogueFactory {
    public static Dialogue createNewDialogue(AIAssistantId aiAssistantId) {
        return new Dialogue(Identity.generate(DialogueId.class),aiAssistantId);
    }

    public static Dialogue reconstitute(DialogueId dialogueId, AIAssistantId aiAssistantId, ArrayList<DialogueContext> dialogueContexts) {
        Dialogue dialogue = new Dialogue(dialogueId,aiAssistantId);
        for (DialogueContext dialogueContext : dialogueContexts){
            dialogue.addContext(dialogueContext);
        }
        return dialogue;
    }
}
