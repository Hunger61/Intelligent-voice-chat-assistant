// command/HangupCommand.java
package host.hunger.vocalchat.infrastructure.external.speech.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HangupCommand extends BaseCommand {

    @JsonProperty("reason")
    private String reason;

    public HangupCommand() {
        setCommand("hangup");
    }

    public HangupCommand(String reason) {
        this();
        this.reason = reason;
    }
}
