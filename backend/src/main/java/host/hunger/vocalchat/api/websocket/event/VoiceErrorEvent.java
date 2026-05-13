package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoiceErrorEvent extends Event {
    @JsonProperty("payload")
    private Payload payload;

    public VoiceErrorEvent(Long seq, String code, String message, boolean recoverable) {
        super("voice.error", seq);
        this.payload = new Payload(code, message, recoverable);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("code")
        private String code;

        @JsonProperty("message")
        private String message;

        @JsonProperty("recoverable")
        private boolean recoverable;
    }
}
