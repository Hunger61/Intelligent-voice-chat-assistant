package host.hunger.vocalchat.domain.event;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.ApplicationEvent;

@Getter
public abstract class DomainEvent extends ApplicationEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    public DomainEvent(Object source) {
        super(source);
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }
}
