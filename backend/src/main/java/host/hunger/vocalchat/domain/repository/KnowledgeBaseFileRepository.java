package host.hunger.vocalchat.domain.repository;

import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseFile;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseFileId;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;

import java.util.List;

public interface KnowledgeBaseFileRepository extends Repository<KnowledgeBaseFile, KnowledgeBaseFileId> {
    List<KnowledgeBaseFile> findByKnowledgeBaseId(KnowledgeBaseId knowledgeBaseId);
    void deleteByKnowledgeBaseId(KnowledgeBaseId knowledgeBaseId);
}
