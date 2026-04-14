package host.hunger.vocalchat.domain.dto.request;

import host.hunger.vocalchat.domain.model.dialogue.DialogueContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuestionRequest {
    private List<DialogueContext> messages;
    private boolean enableOnlineSearch;
}
