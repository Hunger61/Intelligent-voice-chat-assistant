package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoiceAsrInterimEvent extends Event {
    @JsonProperty("payload")
    private Payload payload;

    public VoiceAsrInterimEvent(Long seq, String text, long timestamp) {
        super("voice.asr.interim", seq);
        this.payload = new Payload(text, timestamp);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("text")
        private String text;

        @JsonProperty("timestamp")
        private long timestamp;
    }
}
