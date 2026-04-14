package host.hunger.vocalchat.infrastructure.repository.impl;

import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBase;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;
import host.hunger.vocalchat.domain.repository.KnowledgeBaseRepository;
import host.hunger.vocalchat.infrastructure.repository.persistence.mapper.KnowledgeBaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//todo
@Repository
@RequiredArgsConstructor
public class KnowledgeBaseRepositoryImpl implements KnowledgeBaseRepository {
    private final KnowledgeBaseMapper knowledgeBaseMapper;

    @Override
    public Optional<KnowledgeBaseId> findById(KnowledgeBase knowledgeBase) {
        return Optional.empty();
    }

    @Override
    public List<KnowledgeBaseId> findAll() {
        return List.of();
    }

    @Override
    public void delete(KnowledgeBase knowledgeBase) {

    }

    @Override
    public void save(KnowledgeBaseId entity) {

    }

    @Override
    public boolean exists(KnowledgeBase knowledgeBase) {
        return false;
    }
}
