// command/InviteCommand.java
package host.hunger.vocalchat.infrastructure.external.speech.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import host.hunger.vocalchat.infrastructure.external.speech.option.CallOption;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InviteCommand extends BaseCommand {

    @JsonProperty("option")
    private CallOption option;

    public InviteCommand() {
        setCommand("invite");
    }

    public InviteCommand(CallOption option) {
        this();
        this.option = option;
    }
}
