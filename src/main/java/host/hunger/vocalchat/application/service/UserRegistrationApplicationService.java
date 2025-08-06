package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.factory.UserFactory;
import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.domain.model.user.UserEmail;
import host.hunger.vocalchat.domain.model.user.UserName;
import host.hunger.vocalchat.domain.model.user.UserPassword;
import host.hunger.vocalchat.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationApplicationService {

    private final UserRepository userRepository;

    public void registerUser(String username, String password, String email) {
//        if (userRepository.exists(email)) {
//            throw new RuntimeException("User already exists");
//        }
        //todo
        User user = UserFactory.createWithGeneratedId(new UserName(username), new UserEmail(email) ,new UserPassword(password));
        userRepository.save(user);
        //todo
    }
}
