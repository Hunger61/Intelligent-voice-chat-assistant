package host.hunger.vocalchat.domain.service;

import host.hunger.vocalchat.domain.model.speech.SpeechProcessor;
import host.hunger.vocalchat.domain.model.user.UserId;

//todo
public interface SpeechDomainService {
    SpeechProcessor registerSpeechProcessor(UserId userId);
    void unregisterSpeechProcessor(SpeechProcessor speechProcessor);
    void TextToSpeech(String text, SpeechProcessor speechProcessor);
}
