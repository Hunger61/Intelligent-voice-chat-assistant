package host.hunger.vocalchat.domain.model.shared;

import host.hunger.vocalchat.domain.event.DomainEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AggregateRoot<T> extends Entity<T> {

    private List<DomainEvent> domainEvents = new ArrayList<>();

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    protected void publishDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }
}
