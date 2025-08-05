package host.hunger.vocalchat.infrastructure.persistence.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.infrastructure.persistence.repository.mapper.AIAssistantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AIAssistantRepositoryImpl implements AIAssistantRepository {

    private final AIAssistantMapper aiAssistantMapper;
    @Override
    public AIAssistant findById(AIAssistantId aiAssistantId) {
        return aiAssistantMapper.selectById(aiAssistantId.toString());
    }

    @Override
    public List<AIAssistant> findAll() {
        return aiAssistantMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public void delete(AIAssistantId aiAssistantId) {
        aiAssistantMapper.deleteById(aiAssistantId.toString());
    }

    @Override
    public void save(AIAssistant entity) {
        if(entity.getId() != null){
            aiAssistantMapper.updateById(entity);
        }else{
            aiAssistantMapper.insert(entity);
        }
    }

    @Override
    public boolean exists(AIAssistantId aiAssistantId) {
        return aiAssistantMapper.selectById(aiAssistantId.toString()) != null;
    }
}
