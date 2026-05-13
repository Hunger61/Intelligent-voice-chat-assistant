package host.hunger.vocalchat.api.websocket.event;

import lombok.Getter;

@Getter
public class VoiceChatException extends RuntimeException {

    private final String code;
    private final boolean recoverable;

    public VoiceChatException(String code, String message, boolean recoverable) {
        super(message);
        this.code = code;
        this.recoverable = recoverable;
    }

    public Event toEvent() {
        return new VoiceErrorEvent(null, code, getMessage(), recoverable);
    }
}
