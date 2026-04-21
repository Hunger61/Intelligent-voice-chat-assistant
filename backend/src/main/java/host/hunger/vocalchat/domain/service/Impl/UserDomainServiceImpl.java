package host.hunger.vocalchat.domain.service.impl;

import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.domain.model.user.UserEmail;
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

    @Override
    public User authenticate(UserEmail email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorEnum.EMAIL_NOT_FOUND));
        if (!user.verifyPassword(password)) {
            throw new BaseException(ErrorEnum.PASSWORD_INCORRECT);
        }
        return user;
    }

    // @Override
    // public void addDefaultAIAssistants(UserId userId) {
    //     AIAssistant aiAssistant = AIAssistantFactory.createDefaultAIAssistant();
    //     aiAssistant.setUserId(userId);
    //     aiAssistantRepository.save(aiAssistant);
    // }
}
