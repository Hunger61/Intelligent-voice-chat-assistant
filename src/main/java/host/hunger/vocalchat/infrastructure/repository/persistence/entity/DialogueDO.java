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
@TableName("dialogue")
public class DialogueDO {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("ai_assistant_id")
    private String aiAssistantId;

    /**
     * 按照简化方案把对话上下文序列化为 JSON 字符串保存（建议使用 TEXT）
     * 格式可由 repository 层负责序列化/反序列化为 List<DialogueContext>。
     */
    @TableField("contexts")
    private String contexts;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
