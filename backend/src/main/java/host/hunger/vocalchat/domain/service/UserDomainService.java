package host.hunger.vocalchat.domain.service;

import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.domain.model.user.UserEmail;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.model.user.UserPassword;

public interface UserDomainService {
    User authenticate(UserEmail email, String password);
    void addDefaultAIAssistants(UserId userId);
}
