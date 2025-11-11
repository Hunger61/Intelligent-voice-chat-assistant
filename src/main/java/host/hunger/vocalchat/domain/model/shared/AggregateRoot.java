package host.hunger.vocalchat.domain.model.shared;

import host.hunger.vocalchat.domain.event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public abstract class AggregateRoot<T extends Identity> extends Entity<T> {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected AggregateRoot(T t) {
        super(t);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    protected void publishDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }
}
