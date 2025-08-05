package host.hunger.vocalchat.api.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRecEvent {
    @JsonProperty("event")
    private String eventName;
    @JsonProperty("chat_id")
    private String ChatId;
    @JsonProperty("content")
    private String content;
}
