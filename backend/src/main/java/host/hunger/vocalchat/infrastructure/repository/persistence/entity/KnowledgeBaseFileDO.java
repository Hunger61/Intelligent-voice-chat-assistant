package host.hunger.vocalchat.infrastructure.repository.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("knowledge_base_file")
public class KnowledgeBaseFileDO {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("knowledge_base_id")
    private String knowledgeBaseId;

    @TableField("file_name")
    private String fileName;

    @TableField("file_type")
    private String fileType;

    @TableField("file_size")
    private Long fileSize;

    @TableField("storage_key")
    private String storageKey;

    @TableField("status")
    private String status;

    @TableField("chunk_count")
    private Integer chunkCount;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
