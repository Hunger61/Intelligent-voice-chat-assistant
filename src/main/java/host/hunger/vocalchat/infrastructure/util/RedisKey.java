package host.hunger.vocalchat.infrastructure.util;

import host.hunger.vocalchat.domain.model.user.UserEmail;
import host.hunger.vocalchat.domain.model.user.UserId;
import org.springframework.stereotype.Component;

@Component
public class RedisKey {
    private static final String PREFIX = "vocalchat:";

    // 验证码相关
    public static final String VERIFICATION_CODE_PREFIX = PREFIX + "verification:";
    public static final String USER_PREFIX = PREFIX + "user:";


    /**
     * 构造验证码key
     *
     * @param email 邮箱
     * @return 完整的Redis key
     */
    public static String verificationCodeKey(UserEmail email) {
        return VERIFICATION_CODE_PREFIX + email.getEmail();
    }

    public static String userKey(UserId userId) {
        return USER_PREFIX + userId.toString();
    }
}