package host.hunger.vocalchat.domain.repository;

import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.domain.model.user.UserEmail;
import host.hunger.vocalchat.domain.model.user.UserId;

public interface UserRepository extends Repository<User, UserId>{
    User findByEmail(UserEmail email);
}
