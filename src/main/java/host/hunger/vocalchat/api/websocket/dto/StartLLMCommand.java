package host.hunger.vocalchat.api.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StartLLMCommand extends Command{
    @JsonProperty("ai_assistant_id")
    private String aiAssistantId;
}
