package host.hunger.vocalchat.domain.model.shared;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public abstract class Entity<T> {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    protected T id;
}
