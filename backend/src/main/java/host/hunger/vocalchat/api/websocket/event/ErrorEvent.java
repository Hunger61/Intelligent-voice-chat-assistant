package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorEvent {
    @JsonProperty("event")
    private String eventName;
    @JsonProperty("payload")
    private String errorMessage;
}
