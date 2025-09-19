package host.hunger.vocalchat.domain.factory;

import host.hunger.vocalchat.domain.model.shared.Identity;
import host.hunger.vocalchat.domain.model.user.*;
import org.springframework.stereotype.Component;

@Component
public class UserFactory{
    public static User create(NickName name, UserEmail email, UserPassword password)
    {
        return new User(name, email, password);
    }
    public static User createWithGeneratedId(NickName name, UserEmail email, UserPassword password)
    {
        User user = create(name, email, password);
        user.setId(Identity.generate(UserId.class));
        return new User(name, email, password);
    }
}
