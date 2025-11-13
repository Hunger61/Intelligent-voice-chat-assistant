package host.hunger.vocalchat.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import host.hunger.vocalchat.domain.factory.UserFactory;
import host.hunger.vocalchat.domain.model.user.*;
import host.hunger.vocalchat.domain.repository.UserRepository;
import host.hunger.vocalchat.infrastructure.repository.persistence.entity.UserDO;
import host.hunger.vocalchat.infrastructure.repository.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
//todo
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    @Override
    public Optional<User> findById(UserId userId) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void delete(UserId userId) {

    }

    @Override
    public void save(User user) {
        userMapper.insert(toDataObject(user));
    }

    @Override
    public boolean exists(UserId userId) {
        return false;
    }


    @Override
    public Optional<User> findByEmail(UserEmail userEmail) {
        UserDO userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getEmail, userEmail.getEmail()));
        return Optional.ofNullable(toDomain(userDO));
    }

    @Override
    public boolean existsByEmail(UserEmail email) {
        return userMapper.exists(new LambdaQueryWrapper<UserDO>().eq(UserDO::getEmail, email.getEmail()));
    }

    private User toDomain(UserDO userDO) {
        if (userDO== null){
            return null;
        }
        return UserFactory.reconstitute(new UserId(userDO.getId()),new NickName(userDO.getName()), new UserEmail(userDO.getEmail()), new UserPassword(userDO.getPassword()));
    }

    private UserDO toDataObject(User user) {
        if (user == null){
            return null;
        }
        UserDO userDO = new UserDO();
        userDO.setId(user.getId().toString());
        userDO.setName(user.getNickName().getNickName());
        userDO.setEmail(user.getEmail().getEmail());
        userDO.setPassword(user.getPassword().getPassword());
        return userDO;
    }
}
