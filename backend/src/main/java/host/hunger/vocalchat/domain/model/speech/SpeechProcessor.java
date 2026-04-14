package host.hunger.vocalchat.domain.model.speech;

import host.hunger.vocalchat.domain.model.shared.AggregateRoot;
import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.Getter;
import lombok.Setter;

//todo
@Getter
@Setter
public class SpeechProcessor extends AggregateRoot<SessionId> {

    private UserId userId;
    private Session session;

    public SpeechProcessor(SessionId sessionId, UserId userId) {
        super(sessionId);
        this.userId = userId;
    }

    public SpeechProcessor() {

    }
    //todo
}
