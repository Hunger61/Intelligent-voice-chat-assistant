// command/HistoryCommand.java
package host.hunger.vocalchat.infrastructure.external.speech.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HistoryCommand extends BaseCommand {

    @JsonProperty("speaker")
    private String speaker;

    @JsonProperty("text")
    private String text;

    public HistoryCommand() {
        setCommand("history");
    }

    public HistoryCommand(String speaker, String text) {
        this();
        this.speaker = speaker;
        this.text = text;
    }
}
