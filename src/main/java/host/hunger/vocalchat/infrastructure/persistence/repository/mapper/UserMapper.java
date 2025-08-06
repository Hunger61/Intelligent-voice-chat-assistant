package host.hunger.vocalchat.infrastructure.persistence.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import host.hunger.vocalchat.infrastructure.persistence.repository.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
