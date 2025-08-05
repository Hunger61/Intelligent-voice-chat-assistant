package host.hunger.vocalchat.domain.model.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public abstract class DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
}
