package host.hunger.vocalchat.domain.model.speech;

import host.hunger.vocalchat.domain.model.shared.ValueObject;

//todo
public class SessionId extends ValueObject {
    private String sessionId;
    public SessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
