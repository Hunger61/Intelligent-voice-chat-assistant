package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoiceTtsInterruptedEvent extends Event {
    @JsonProperty("payload")
    private Payload payload;

    public VoiceTtsInterruptedEvent(Long seq, String chatId, String reason) {
        super("voice.tts.interrupted", seq);
        this.payload = new Payload(chatId, reason);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("chatId")
        private String chatId;

        @JsonProperty("reason")
        private String reason;
    }
}
