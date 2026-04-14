package host.hunger.vocalchat.domain.event;

import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class QuestionReceivedEvent extends DomainEvent{
    public QuestionReceivedEvent(String question, UserId userId) {
        super(UUID.randomUUID().toString(),LocalDateTime.now());
        this.question = question;
        this.userId = userId;
    }

    private final String question;
    private final UserId userId;
}
