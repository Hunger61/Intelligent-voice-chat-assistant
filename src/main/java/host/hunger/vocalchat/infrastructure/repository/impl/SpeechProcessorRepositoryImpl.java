package host.hunger.vocalchat.infrastructure.repository.impl;

import host.hunger.vocalchat.domain.model.speech.SpeechProcessor;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.SpeechProcessorRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SpeechProcessorRepositoryImpl implements SpeechProcessorRepository {
    Map<UserId, SpeechProcessor> speechProcessors = new HashMap<>();

    public SpeechProcessor findById(UserId userId) {
        return speechProcessors.get(userId);
    }

    public void save(UserId userId,SpeechProcessor entity) {
        speechProcessors.put(userId, entity);
    }
}
