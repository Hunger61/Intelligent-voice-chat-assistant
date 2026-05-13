package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoiceAsrFinalEvent extends Event {
    @JsonProperty("payload")
    private Payload payload;

    public VoiceAsrFinalEvent(Long seq, String text, long timestamp, long duration) {
        super("voice.asr.final", seq);
        this.payload = new Payload(text, timestamp, duration);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("text")
        private String text;

        @JsonProperty("timestamp")
        private long timestamp;

        @JsonProperty("duration")
        private long duration;
    }
}
