package host.hunger.vocalchat.domain.model.knowledgeabase;

import host.hunger.vocalchat.domain.model.shared.AggregateRoot;
import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@SuppressWarnings("unused")
public class KnowledgeBase extends AggregateRoot<KnowledgeBaseId> {
    private final KnowledgeBaseId knowledgeBaseId;
    private final KnowledgeBaseName knowledgeBaseName;
    private final KnowledgeBaseDescription knowledgeBaseDescription;
    private UserId userId;


    /**
     * 轻量状态字段，建议后续枚举化为 ACTIVE / ARCHIVED / DISABLED 等。
     */
    private String status;//todo

    /**
     * 当前知识库下的文档数量，用于列表展示和配额控制。
     */
    private Integer documentCount;

    /**
     * 当前知识库下的切片数量，用于检索和统计展示。
     */
    private Integer chunkCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public KnowledgeBase(KnowledgeBaseId knowledgeBaseId, UserId userId, KnowledgeBaseName knowledgeBaseName, String description, KnowledgeBaseId knowledgeBaseId1, KnowledgeBaseName knowledgeBaseIdName, KnowledgeBaseDescription knowledgeBaseDescription) {
        super(knowledgeBaseId);
        this.userId = userId;
        this.knowledgeBaseId = knowledgeBaseId1;
        this.knowledgeBaseName = knowledgeBaseName;
        this.knowledgeBaseDescription = knowledgeBaseDescription;
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
