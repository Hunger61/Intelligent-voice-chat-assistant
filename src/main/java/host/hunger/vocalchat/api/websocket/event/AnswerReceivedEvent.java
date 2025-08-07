package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnswerReceivedEvent {
    @JsonProperty("event")
    private String eventName;
    @JsonProperty("chat_id")
    private String ChatId;
    @JsonProperty("content")
    private String content;
}
