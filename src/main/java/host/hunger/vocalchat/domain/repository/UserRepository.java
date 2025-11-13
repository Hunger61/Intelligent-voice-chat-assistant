package host.hunger.vocalchat.domain.repository;

import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.domain.model.user.UserEmail;
import host.hunger.vocalchat.domain.model.user.UserId;

import java.util.Optional;

public interface UserRepository extends Repository<User, UserId>{
    Optional<User> findByEmail(UserEmail email);
    boolean existsByEmail(UserEmail email);
}
