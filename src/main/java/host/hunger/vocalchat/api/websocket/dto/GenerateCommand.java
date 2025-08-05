package host.hunger.vocalchat.api.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GenerateCommand extends Command{
    @JsonProperty("content")
    private String content;
}
