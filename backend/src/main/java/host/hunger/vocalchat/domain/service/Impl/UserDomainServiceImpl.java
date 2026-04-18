package host.hunger.vocalchat.domain.service.impl;

import host.hunger.vocalchat.domain.enums.DefaultAIAssistants;
import host.hunger.vocalchat.domain.factory.AIAssistantFactory;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.domain.model.user.UserEmail;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.domain.repository.UserRepository;
import host.hunger.vocalchat.domain.service.UserDomainService;
import host.hunger.vocalchat.infrastructure.exception.BaseException;
import host.hunger.vocalchat.shared.enums.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;
    private final AIAssistantRepository aiAssistantRepository;

    @Override
    public User authenticate(UserEmail email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorEnum.EMAIL_NOT_FOUND));
        if (!user.verifyPassword(password)) {
            throw new BaseException(ErrorEnum.PASSWORD_INCORRECT);
        }
        return user;
    }

    @Override
    public void addDefaultAIAssistants(UserId userId) {
        AIAssistant aiAssistant = AIAssistantFactory.createDefaultAIAssistant(DefaultAIAssistants.XIAO_ZHI);
        aiAssistant.setUserId(userId);
        aiAssistantRepository.save(aiAssistant);
    }
}
