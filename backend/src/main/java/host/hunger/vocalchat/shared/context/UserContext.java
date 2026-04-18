package host.hunger.vocalchat.shared.context;

import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.infrastructure.exception.BaseException;
import host.hunger.vocalchat.shared.enums.ErrorEnum;

import java.util.Optional;

public final class UserContext {

    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(User user) {
        CURRENT_USER.set(user);
    }

    public static User get() {
        return CURRENT_USER.get();
    }

    public static User require() {
        return Optional.ofNullable(get()).orElseThrow(() -> new BaseException(ErrorEnum.NO_LOGIN));
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
