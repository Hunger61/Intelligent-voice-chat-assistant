package host.hunger.vocalchat.domain.model.speech;

import host.hunger.vocalchat.domain.model.shared.AggregateRoot;
import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

//todo
@Getter
@Setter
@RequiredArgsConstructor
public class SpeechProcessor extends AggregateRoot<SessionId> {

    private final UserId userId;
    private Session session;
    //todo
}
