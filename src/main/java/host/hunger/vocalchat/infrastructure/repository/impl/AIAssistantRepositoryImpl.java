package host.hunger.vocalchat.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import host.hunger.vocalchat.domain.model.aiassistant.*;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.infrastructure.repository.persistence.entity.AIAssistantDO;
import host.hunger.vocalchat.infrastructure.repository.persistence.mapper.AIAssistantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AIAssistantRepositoryImpl implements AIAssistantRepository {

    private final AIAssistantMapper aiAssistantMapper;
    @Override
    public Optional<AIAssistant> findById(AIAssistantId aiAssistantId) {
        return Optional.ofNullable(toDomain(aiAssistantMapper.selectById(aiAssistantId.toString())));
    }

    @Override
    public List<AIAssistant> findAll() {
        return toDomain(aiAssistantMapper.selectList(new QueryWrapper<>()));
    }

    @Override
    public void delete(AIAssistantId aiAssistantId) {
        aiAssistantMapper.deleteById(aiAssistantId.toString());
    }

    @Override
    public void save(AIAssistant aiAssistant) {
        if(aiAssistant.getId() != null && !aiAssistant.getId().toString().isBlank()){
            aiAssistantMapper.updateById(toDataObject(aiAssistant));
        }else{
            aiAssistantMapper.insert(toDataObject(aiAssistant));
        }
    }

    @Override
    public boolean exists(AIAssistantId aiAssistantId) {
        return aiAssistantMapper.selectById(aiAssistantId.toString()) != null;
    }

    private AIAssistant toDomain(AIAssistantDO assistantDO) {
        if (assistantDO== null){
            return null;
        }
        return new AIAssistant(
                new AIAssistantId(assistantDO.getId()),
                new UserId(assistantDO.getUserId()),
                new AIAssistantName(assistantDO.getName()),
                new AIAssistantDescription(assistantDO.getDescription()),
                new AIAssistantCharacter(assistantDO.getAssistantCharacter())
        );
    }

    private AIAssistantDO toDataObject(AIAssistant assistant) {
        if (assistant == null){
            return null;
        }
        AIAssistantDO assistantDO = new AIAssistantDO();
        assistantDO.setId(assistant.getId().toString());
        assistantDO.setUserId(assistant.getUserId().toString());
        assistantDO.setName(assistant.getName().toString());
        assistantDO.setDescription(assistant.getDescription().toString());
        assistantDO.setAssistantCharacter(assistant.getAssistantCharacter().toString());
        return assistantDO;
    }

    private List<AIAssistant> toDomain(List<AIAssistantDO> assistantDOs) {
        return assistantDOs.stream().map(this::toDomain).toList();
    }
}
