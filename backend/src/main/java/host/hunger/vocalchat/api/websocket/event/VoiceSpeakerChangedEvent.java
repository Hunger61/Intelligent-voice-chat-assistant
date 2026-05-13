package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoiceSpeakerChangedEvent extends Event {
    @JsonProperty("payload")
    private Payload payload;

    public VoiceSpeakerChangedEvent(Long seq, String userId, boolean isSpeaking) {
        super("voice.speaker.changed", seq);
        this.payload = new Payload(userId, isSpeaking);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("userId")
        private String userId;

        @JsonProperty("isSpeaking")
        private boolean isSpeaking;
    }
}
