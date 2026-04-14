package host.hunger.vocalchat.infrastructure.cache.redis;

import host.hunger.vocalchat.domain.model.user.UserEmail;
import host.hunger.vocalchat.domain.model.user.UserId;

public final class RedisKeys {
    private static final String PREFIX = "vocalchat:";

    // 验证码相关
    public static final String VERIFICATION_CODE_PREFIX = PREFIX + "verification:";
    public static final String USER_PREFIX = PREFIX + "user:";

    // 对象存储相关
    public static final String OBJECT_STORAGE_PREFIX = PREFIX + "object-storage:";
    public static final String PRESIGNED_URL_PREFIX = OBJECT_STORAGE_PREFIX + "presigned:";

    private RedisKeys() {
        // Utility class
    }



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

    /**
     * 构造对象存储业务前缀: {bucket}/{userId}/{category}/
     */
    public static String objectStoragePathPrefix(String bucket, UserId userId, String category) {
        return bucket + "/" + userId + "/" + category + "/";
    }

    /**
     * 构造预签名URL缓存key: vocalchat:object-storage:presigned:{bucket}:{userId}:{category}:{objectName}
     */
    public static String preSignedUrlKey(String bucket, UserId userId, String category, String objectName) {
        return PRESIGNED_URL_PREFIX + bucket + ":" + userId + ":" + category + ":" + objectName;
    }

    /**
     * 兼容旧调用方式
     */
    public static String preSignedUrlKey(String objectName) {
        return PRESIGNED_URL_PREFIX + objectName;
    }
}
