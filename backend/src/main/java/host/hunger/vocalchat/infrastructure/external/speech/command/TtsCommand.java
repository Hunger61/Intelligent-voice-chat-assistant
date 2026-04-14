// command/TtsCommand.java
package host.hunger.vocalchat.infrastructure.external.speech.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import host.hunger.vocalchat.infrastructure.external.speech.option.TTSOption;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TtsCommand extends BaseCommand {

    @JsonProperty("text")
    private String text;

    @JsonProperty("speaker")
    private String speaker;

    @JsonProperty("playId")
    private String playID;

    @JsonProperty("autoHangup")
    private Boolean autoHangup;

    @JsonProperty("streaming")
    private Boolean streaming;

    @JsonProperty("endOfStream")
    private Boolean endOfStream;

    @JsonProperty("option")
    private TTSOption option;

    public TtsCommand() {
        setCommand("tts");
    }

    public TtsCommand(String text) {
        this();
        this.text = text;
    }

    public TtsCommand(String text, String speaker) {
        this(text);
        this.speaker = speaker;
    }
}
