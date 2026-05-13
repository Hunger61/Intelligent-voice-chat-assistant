package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoiceSessionStartedEvent extends Event {
    @JsonProperty("payload")
    private Payload payload;

    public VoiceSessionStartedEvent(Long seq, String sessionId) {
        super("voice.session.started", seq);
        this.payload = new Payload(sessionId);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("sessionId")
        private String sessionId;
    }
}
