package host.hunger.vocalchat.infrastructure.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorEnum {

    COMMON_ERROR("系统异常", 1000),
    VERIFICATION_CODE_INCORRECT("验证码不正确", 1001),
    EMAIL_ALREADY_USED("邮箱已被使用", 1002),
    EMAIL_NOT_FOUND("邮箱未找到", 1003),
    PASSWORD_INCORRECT("密码不正确", 1004),
    AI_ASSISTANT_ID_NULL("AI 助手 ID 为空", 2001),
    AI_ASSISTANT_NOT_FOUND("AI 助手不存在", 2002);

    private final String message;
    private final Integer code;
}
