package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ASREvent {
    @JsonProperty("type")
    private String eventName;
    @JsonProperty("payload")
    private String content;
}
