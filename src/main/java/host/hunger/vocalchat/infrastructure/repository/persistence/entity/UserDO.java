package host.hunger.vocalchat.infrastructure.repository.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@TableName("user")
public class UserDO {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableId(value = "name")
    private String name;

    @TableId(value = "email")
    private String email;

    @TableId(value = "password")
    private String password;

    @TableId(value = "created_at")
    private LocalDateTime createdAt;

    @TableId(value = "updated_at")
    private LocalDateTime updatedAt;
}
