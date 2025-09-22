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

@Repository
@RequiredArgsConstructor
//todo
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
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
    public void save(User user) {

    }

    @Override
    public boolean exists(UserId userId) {
        return false;
    }


    @Override
    public User findByEmail(UserEmail userEmail) {
        UserDO userDO = userMapper.selectOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getEmail, userEmail.getEmail()));
        if (userDO == null){
            return null;
        }
        return toDomain(userDO);
    }

    private User toDomain(UserDO userDO) {
        User user = UserFactory.create(new NickName(userDO.getName()), new UserEmail(userDO.getEmail()), new UserPassword(userDO.getPassword()));
        user.setId(new UserId(userDO.getId()));
        return user;
    }

    private UserDO toPersistence(User user) {
        UserDO userDO = new UserDO();
        userDO.setId(user.getId().toString());
        userDO.setName(user.getNickName().getNickName());
        userDO.setEmail(user.getEmail().getEmail());
        userDO.setPassword(user.getPassword().getPassword());
        return userDO;
    }
}
