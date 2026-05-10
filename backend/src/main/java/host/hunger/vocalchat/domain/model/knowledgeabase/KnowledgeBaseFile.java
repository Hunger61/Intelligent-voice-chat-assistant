package host.hunger.vocalchat.domain.model.knowledgeabase;

import host.hunger.vocalchat.domain.model.shared.Entity;
import host.hunger.vocalchat.domain.model.shared.Identity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class KnowledgeBaseFile extends Entity<KnowledgeBaseFileId> {

    private KnowledgeBaseId knowledgeBaseId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String storageKey;
    private String status;
    private Integer chunkCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public KnowledgeBaseFile(KnowledgeBaseId knowledgeBaseId, String fileName,
                              String fileType, Long fileSize, String storageKey) {
        super(Identity.generate(KnowledgeBaseFileId.class));
        this.knowledgeBaseId = knowledgeBaseId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.storageKey = storageKey;
        this.status = "UPLOADING";
        this.chunkCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public KnowledgeBaseFile(KnowledgeBaseFileId id, KnowledgeBaseId knowledgeBaseId,
                              String fileName, String fileType, Long fileSize,
                              String storageKey, String status, Integer chunkCount,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id);
        this.knowledgeBaseId = knowledgeBaseId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.storageKey = storageKey;
        this.status = status;
        this.chunkCount = chunkCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
