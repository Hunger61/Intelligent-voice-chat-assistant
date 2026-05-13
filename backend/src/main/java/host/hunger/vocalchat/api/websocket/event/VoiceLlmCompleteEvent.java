package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoiceLlmCompleteEvent extends Event {
    @JsonProperty("payload")
    private Payload payload;

    public VoiceLlmCompleteEvent(Long seq, String fullText, String chatId, String dialogueId) {
        super("voice.llm.complete", seq);
        this.payload = new Payload(fullText, chatId, dialogueId);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("fullText")
        private String fullText;

        @JsonProperty("chatId")
        private String chatId;

        @JsonProperty("dialogueId")
        private String dialogueId;
    }
}
