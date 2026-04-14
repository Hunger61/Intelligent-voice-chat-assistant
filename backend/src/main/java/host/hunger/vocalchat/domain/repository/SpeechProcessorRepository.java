package host.hunger.vocalchat.domain.repository;

import host.hunger.vocalchat.domain.model.speech.SpeechProcessor;
import host.hunger.vocalchat.domain.model.user.UserId;

public interface SpeechProcessorRepository{

    public SpeechProcessor findById(UserId userId);
    public void save(UserId userId,SpeechProcessor entity);
}
