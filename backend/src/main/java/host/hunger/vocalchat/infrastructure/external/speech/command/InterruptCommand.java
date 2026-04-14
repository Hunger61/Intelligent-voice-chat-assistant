// command/InterruptCommand.java
package host.hunger.vocalchat.infrastructure.external.speech.command;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InterruptCommand extends BaseCommand {

    public InterruptCommand() {
        setCommand("interrupt");
    }
}
