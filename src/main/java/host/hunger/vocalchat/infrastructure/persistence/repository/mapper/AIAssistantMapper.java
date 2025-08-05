package host.hunger.vocalchat.infrastructure.persistence.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AIAssistantMapper extends BaseMapper<AIAssistant> {
}
