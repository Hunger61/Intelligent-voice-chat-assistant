package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoiceLlmThinkingEvent extends Event {
    @JsonProperty("payload")
    private Payload payload;

    public VoiceLlmThinkingEvent(Long seq, String token, int index) {
        super("voice.llm.thinking", seq);
        this.payload = new Payload(token, index);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("token")
        private String token;

        @JsonProperty("index")
        private int index;
    }
}
