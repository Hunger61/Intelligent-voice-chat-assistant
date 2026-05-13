package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoiceTtsEndedEvent extends Event {
    @JsonProperty("payload")
    private Payload payload;

    public VoiceTtsEndedEvent(Long seq, String chatId) {
        super("voice.tts.ended", seq);
        this.payload = new Payload(chatId);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("chatId")
        private String chatId;
    }
}
