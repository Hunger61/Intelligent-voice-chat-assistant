package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoiceSessionEndedEvent extends Event {
    @JsonProperty("payload")
    private Payload payload;

    public VoiceSessionEndedEvent(Long seq, String sessionId, String reason) {
        super("voice.session.ended", seq);
        this.payload = new Payload(sessionId, reason);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("sessionId")
        private String sessionId;

        @JsonProperty("reason")
        private String reason;
    }
}
