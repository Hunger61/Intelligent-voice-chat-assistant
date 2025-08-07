package host.hunger.vocalchat.api.websocket.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StartLLMCommand extends Command{
    @JsonProperty("ai_assistant_id")
    private String aiAssistantId;
}
