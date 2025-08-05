package host.hunger.vocalchat.domain.model.shared;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
