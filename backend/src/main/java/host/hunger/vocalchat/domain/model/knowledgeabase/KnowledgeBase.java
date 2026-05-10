package host.hunger.vocalchat.domain.model.knowledgeabase;

import host.hunger.vocalchat.domain.model.shared.AggregateRoot;
import host.hunger.vocalchat.domain.model.shared.Identity;
import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class KnowledgeBase extends AggregateRoot<KnowledgeBaseId> {

    private KnowledgeBaseName name;
    private KnowledgeBaseDescription description;
    private UserId userId;
    private String status;
    private Integer documentCount;
    private Integer chunkCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public KnowledgeBase(UserId userId, KnowledgeBaseName name, KnowledgeBaseDescription description) {
        super(Identity.generate(KnowledgeBaseId.class));
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.status = "ACTIVE";
        this.documentCount = 0;
        this.chunkCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public KnowledgeBase(KnowledgeBaseId id, UserId userId, KnowledgeBaseName name,
                         KnowledgeBaseDescription description, String status,
                         Integer documentCount, Integer chunkCount,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id);
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.documentCount = documentCount;
        this.chunkCount = chunkCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void modifyConfig(KnowledgeBaseName name, KnowledgeBaseDescription description) {
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean belongsTo(UserId userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    public void increaseDocumentCount() {
        this.documentCount = this.documentCount == null ? 1 : this.documentCount + 1;
    }

    public void decreaseDocumentCount() {
        this.documentCount = this.documentCount == null || this.documentCount <= 0 ? 0 : this.documentCount - 1;
    }

    public void increaseChunkCount() {
        this.chunkCount = this.chunkCount == null ? 1 : this.chunkCount + 1;
    }

    public void decreaseChunkCount() {
        this.chunkCount = this.chunkCount == null || this.chunkCount <= 0 ? 0 : this.chunkCount - 1;
    }
}
