package host.hunger.vocalchat.domain.model.speech;

import host.hunger.vocalchat.domain.model.shared.AggregateRoot;
import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

//todo
@Getter
@Setter
public class SpeechProcessor extends AggregateRoot<SessionId> {

    private final UserId userId;
    private Session session;

    protected SpeechProcessor(SessionId sessionId, UserId userId) {
        super(sessionId);
        this.userId = userId;
    }
    //todo
}
