package host.hunger.vocalchat.domain.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
