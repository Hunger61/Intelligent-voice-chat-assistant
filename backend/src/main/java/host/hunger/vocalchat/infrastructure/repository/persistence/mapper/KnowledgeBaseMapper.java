package host.hunger.vocalchat.infrastructure.repository.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import host.hunger.vocalchat.infrastructure.repository.persistence.entity.KnowledgeBaseDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
//todo 这个mapper还没用到，等后续完善知识库功能时再使用
public interface KnowledgeBaseMapper extends BaseMapper<KnowledgeBaseDO> {
}
