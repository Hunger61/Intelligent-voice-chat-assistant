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
public class VoiceSessionJoinCommand extends Command {
    @JsonProperty("aiAssistantId")
    private String aiAssistantId;

    @JsonProperty("tts")
    private TtsConfig tts;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TtsConfig {
        @JsonProperty("speaker")
        private String speaker;

        @JsonProperty("speed")
        private double speed = 1.0;

        @JsonProperty("volume")
        private int volume = 80;
    }
}
