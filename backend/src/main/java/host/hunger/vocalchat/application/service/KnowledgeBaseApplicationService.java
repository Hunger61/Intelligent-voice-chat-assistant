package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.factory.KnowledgeBaseFactory;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBase;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseDescription;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseName;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.KnowledgeBaseRepository;
import host.hunger.vocalchat.infrastructure.exception.BaseException;
import host.hunger.vocalchat.shared.context.UserContext;
import host.hunger.vocalchat.shared.enums.ErrorEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeBaseApplicationService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    public KnowledgeBase getKnowledgeBaseById(KnowledgeBaseId id) {
        if (id == null) {
            throw new BaseException(ErrorEnum.COMMON_ERROR);
        }
        return knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorEnum.COMMON_ERROR));
    }

    @Transactional
    public void createKnowledgeBase(String name, String description) {
        UserId userId = UserContext.require().getId();
        KnowledgeBaseName kbName = new KnowledgeBaseName(name);
        KnowledgeBaseDescription kbDesc = description != null && !description.isEmpty()
                ? new KnowledgeBaseDescription(description) : null;
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.createNewKnowledgeBase(userId, kbName, kbDesc);
        knowledgeBaseRepository.save(knowledgeBase);
    }

    public List<KnowledgeBase> getKnowledgeBasesByUserId(UserId userId) {
        return knowledgeBaseRepository.findByUserId(userId);
    }

    @Transactional
    public void modifyKnowledgeBase(String knowledgeBaseId, String name, String description) {
        KnowledgeBaseId id = new KnowledgeBaseId(knowledgeBaseId);
        KnowledgeBase knowledgeBase = getKnowledgeBaseById(id);
        KnowledgeBaseName kbName = new KnowledgeBaseName(name);
        KnowledgeBaseDescription kbDesc = description != null && !description.isEmpty()
                ? new KnowledgeBaseDescription(description) : null;
        knowledgeBase.modifyConfig(kbName, kbDesc);
        knowledgeBaseRepository.save(knowledgeBase);
    }

    @Transactional
    public void deleteKnowledgeBase(String knowledgeBaseId) {
        KnowledgeBaseId id = new KnowledgeBaseId(knowledgeBaseId);
        knowledgeBaseRepository.delete(id);
    }
}
