// command/PlayCommand.java
package host.hunger.vocalchat.infrastructure.external.speech.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayCommand extends BaseCommand {

    @JsonProperty("url")
    private String url;

    @JsonProperty("autoHangup")
    private Boolean autoHangup;

    public PlayCommand() {
        setCommand("play");
    }

    public PlayCommand(String url) {
        this();
        this.url = url;
    }
}
