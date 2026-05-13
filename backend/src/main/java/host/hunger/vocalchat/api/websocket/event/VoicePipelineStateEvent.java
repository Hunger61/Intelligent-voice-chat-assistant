package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VoicePipelineStateEvent extends Event {
    @JsonProperty("payload")
    private Payload payload;

    public VoicePipelineStateEvent(Long seq, String state) {
        super("voice.pipeline.state", seq);
        this.payload = new Payload(state);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Payload {
        @JsonProperty("state")
        private String state;
    }
}
