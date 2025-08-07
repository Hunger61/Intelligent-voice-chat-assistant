package host.hunger.vocalchat.infrastructure.repository.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import host.hunger.vocalchat.infrastructure.repository.persistence.entity.AIAssistantDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AIAssistantMapper extends BaseMapper<AIAssistantDO> {
}
