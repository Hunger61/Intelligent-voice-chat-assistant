package host.hunger.vocalchat.domain.event;

import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.Getter;

@Getter
public class QuestionReceivedEvent extends DomainEvent {
    public QuestionReceivedEvent(String question, UserId userId) {
        super(userId.toString() + "QuestionReceivedEvent");// todo
        this.question = question;
        this.userId = userId;
    }

    private final String question;
    private final UserId userId;
}
