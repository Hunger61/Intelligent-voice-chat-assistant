package host.hunger.vocalchat.infrastructure.persistence.repository.impl;

import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//todo
public class UserRepositoryImpl implements UserRepository {
    @Override
    public User findById(UserId userId) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void delete(UserId userId) {

    }

    @Override
    public void save(User entity) {

    }

    @Override
    public boolean exists(UserId userId) {
        return false;
    }
}
