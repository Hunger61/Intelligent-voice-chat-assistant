package host.hunger.vocalchat.domain.repository;

import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBase;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;
import host.hunger.vocalchat.domain.model.user.UserId;

import java.util.List;

public interface KnowledgeBaseRepository extends Repository<KnowledgeBase, KnowledgeBaseId> {
    List<KnowledgeBase> findByUserId(UserId userId);
}
