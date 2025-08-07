package host.hunger.vocalchat.domain.factory;

import host.hunger.vocalchat.domain.model.speech.SpeechProcessor;
import host.hunger.vocalchat.domain.model.user.UserId;
import org.springframework.stereotype.Component;

@Component
public class SpeechProcessorFactory {
    public static SpeechProcessor create(UserId userId) {
        return new SpeechProcessor(userId);
    }
}
