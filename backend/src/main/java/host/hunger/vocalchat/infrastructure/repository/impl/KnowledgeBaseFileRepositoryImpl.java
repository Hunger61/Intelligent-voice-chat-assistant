package host.hunger.vocalchat.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseFile;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseFileId;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;
import host.hunger.vocalchat.domain.repository.KnowledgeBaseFileRepository;
import host.hunger.vocalchat.infrastructure.repository.persistence.entity.KnowledgeBaseFileDO;
import host.hunger.vocalchat.infrastructure.repository.persistence.mapper.KnowledgeBaseFileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class KnowledgeBaseFileRepositoryImpl implements KnowledgeBaseFileRepository {

    private final KnowledgeBaseFileMapper knowledgeBaseFileMapper;

    @Override
    public Optional<KnowledgeBaseFile> findById(KnowledgeBaseFileId id) {
        return Optional.ofNullable(toDomain(knowledgeBaseFileMapper.selectById(id.toString())));
    }

    @Override
    public List<KnowledgeBaseFile> findAll() {
        return toDomain(knowledgeBaseFileMapper.selectList(new QueryWrapper<>()));
    }

    @Override
    public List<KnowledgeBaseFile> findByKnowledgeBaseId(KnowledgeBaseId knowledgeBaseId) {
        return toDomain(knowledgeBaseFileMapper.selectList(
                new LambdaQueryWrapper<KnowledgeBaseFileDO>()
                        .eq(KnowledgeBaseFileDO::getKnowledgeBaseId, knowledgeBaseId.toString())));
    }

    @Override
    public void delete(KnowledgeBaseFileId id) {
        knowledgeBaseFileMapper.deleteById(id.toString());
    }

    @Override
    public void deleteByKnowledgeBaseId(KnowledgeBaseId knowledgeBaseId) {
        knowledgeBaseFileMapper.delete(new LambdaQueryWrapper<KnowledgeBaseFileDO>()
                .eq(KnowledgeBaseFileDO::getKnowledgeBaseId, knowledgeBaseId.toString()));
    }

    @Override
    public void save(KnowledgeBaseFile file) {
        KnowledgeBaseFileDO fileDO = toDataObject(file);
        if (exists(file.getId())) {
            knowledgeBaseFileMapper.updateById(fileDO);
        } else {
            knowledgeBaseFileMapper.insert(fileDO);
        }
    }

    @Override
    public boolean exists(KnowledgeBaseFileId id) {
        return knowledgeBaseFileMapper.selectById(id.toString()) != null;
    }

    private KnowledgeBaseFile toDomain(KnowledgeBaseFileDO d) {
        if (d == null) {
            return null;
        }
        return new KnowledgeBaseFile(
                new KnowledgeBaseFileId(d.getId()),
                new KnowledgeBaseId(d.getKnowledgeBaseId()),
                d.getFileName(),
                d.getFileType(),
                d.getFileSize(),
                d.getStorageKey(),
                d.getStatus(),
                d.getChunkCount(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }

    private KnowledgeBaseFileDO toDataObject(KnowledgeBaseFile f) {
        if (f == null) {
            return null;
        }
        KnowledgeBaseFileDO d = new KnowledgeBaseFileDO();
        if (f.getId() != null) {
            d.setId(f.getId().toString());
        }
        if (f.getKnowledgeBaseId() != null) {
            d.setKnowledgeBaseId(f.getKnowledgeBaseId().toString());
        }
        d.setFileName(f.getFileName());
        d.setFileType(f.getFileType());
        d.setFileSize(f.getFileSize());
        d.setStorageKey(f.getStorageKey());
        d.setStatus(f.getStatus());
        d.setChunkCount(f.getChunkCount());
        d.setCreatedAt(f.getCreatedAt());
        d.setUpdatedAt(f.getUpdatedAt());
        return d;
    }

    private List<KnowledgeBaseFile> toDomain(List<KnowledgeBaseFileDO> dos) {
        return dos.stream().map(this::toDomain).toList();
    }
}
