package host.hunger.vocalchat.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBase;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseDescription;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseName;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.KnowledgeBaseRepository;
import host.hunger.vocalchat.infrastructure.repository.persistence.entity.KnowledgeBaseDO;
import host.hunger.vocalchat.infrastructure.repository.persistence.mapper.KnowledgeBaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class KnowledgeBaseRepositoryImpl implements KnowledgeBaseRepository {

    private final KnowledgeBaseMapper knowledgeBaseMapper;

    @Override
    public Optional<KnowledgeBase> findById(KnowledgeBaseId id) {
        return Optional.ofNullable(toDomain(knowledgeBaseMapper.selectById(id.toString())));
    }

    @Override
    public List<KnowledgeBase> findAll() {
        return toDomain(knowledgeBaseMapper.selectList(new QueryWrapper<>()));
    }

    @Override
    public List<KnowledgeBase> findByUserId(UserId userId) {
        return toDomain(knowledgeBaseMapper.selectList(
                new LambdaQueryWrapper<KnowledgeBaseDO>().eq(KnowledgeBaseDO::getUserId, userId.toString())));
    }

    @Override
    public void delete(KnowledgeBaseId id) {
        knowledgeBaseMapper.deleteById(id.toString());
    }

    @Override
    public void save(KnowledgeBase knowledgeBase) {
        KnowledgeBaseDO knowledgeBaseDO = toDataObject(knowledgeBase);
        if (exists(knowledgeBase.getId())) {
            knowledgeBaseMapper.updateById(knowledgeBaseDO);
        } else {
            knowledgeBaseMapper.insert(knowledgeBaseDO);
        }
    }

    @Override
    public boolean exists(KnowledgeBaseId id) {
        return knowledgeBaseMapper.selectById(id.toString()) != null;
    }

    private KnowledgeBase toDomain(KnowledgeBaseDO d) {
        if (d == null) {
            return null;
        }
        return new KnowledgeBase(
                new KnowledgeBaseId(d.getId()),
                new UserId(d.getUserId()),
                new KnowledgeBaseName(d.getName()),
                d.getDescription() != null && !d.getDescription().isEmpty() ? new KnowledgeBaseDescription(d.getDescription()) : null,
                d.getStatus(),
                d.getDocumentCount(),
                d.getChunkCount(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }

    private KnowledgeBaseDO toDataObject(KnowledgeBase kb) {
        if (kb == null) {
            return null;
        }
        KnowledgeBaseDO d = new KnowledgeBaseDO();
        if (kb.getId() != null) {
            d.setId(kb.getId().toString());
        }
        if (kb.getUserId() != null) {
            d.setUserId(kb.getUserId().toString());
        }
        d.setName(kb.getName().getName());
        d.setDescription(kb.getDescription() != null ? kb.getDescription().getDescription() : null);
        d.setStatus(kb.getStatus());
        d.setDocumentCount(kb.getDocumentCount());
        d.setChunkCount(kb.getChunkCount());
        d.setCreatedAt(kb.getCreatedAt());
        d.setUpdatedAt(kb.getUpdatedAt());
        return d;
    }

    private List<KnowledgeBase> toDomain(List<KnowledgeBaseDO> dos) {
        return dos.stream().map(this::toDomain).toList();
    }
}
