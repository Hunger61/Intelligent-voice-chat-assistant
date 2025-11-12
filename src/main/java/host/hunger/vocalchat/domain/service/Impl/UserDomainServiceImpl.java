package host.hunger.vocalchat.domain.service.Impl;

import host.hunger.vocalchat.domain.factory.AIAssistantFactory;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.domain.model.user.UserEmail;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.model.user.UserPassword;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.domain.repository.UserRepository;
import host.hunger.vocalchat.domain.service.UserDomainService;
import host.hunger.vocalchat.infrastructure.Enum.DefaultAIAssistants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;
    private final AIAssistantRepository aiAssistantRepository;

    @Override
    public User authenticate(UserEmail email, UserPassword password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email not found.");
        }
        if (user.verifyPassword(password)) {
            throw new RuntimeException("Password is incorrect.");
        }
        return user;
    }

    @Override
    public void addDefaultAIAssistants(UserId userId) {
        AIAssistant aiAssistant = AIAssistantFactory.createDeaultAIAssistant(DefaultAIAssistants.XIAO_ZHI);
        aiAssistant.setUserId(userId);
        aiAssistantRepository.save(aiAssistant);
    }
}
