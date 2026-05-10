package host.hunger.vocalchat.api.rest.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseFileVO implements Serializable {
    private String id;
    private String knowledgeBaseId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String status;
    private Integer chunkCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
