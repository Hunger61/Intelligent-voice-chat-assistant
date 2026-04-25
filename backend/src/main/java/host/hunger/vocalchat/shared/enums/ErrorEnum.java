package host.hunger.vocalchat.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorEnum {

    COMMON_ERROR("系统异常，请联系管理员", 1000),
    VERIFICATION_CODE_INCORRECT("验证码不正确", 1001),
    EMAIL_ALREADY_USED("邮箱已被使用", 1002),
    EMAIL_NOT_FOUND("邮箱未找到", 1003),
    PASSWORD_INCORRECT("密码不正确", 1004),
    AI_ASSISTANT_ID_NULL("AI 助手 ID 为空", 2001),
    AI_ASSISTANT_NOT_FOUND("AI 助手不存在", 2002),
    DIALOGUE_NOT_FOUND("对话不存在", 3001),
    NO_LOGIN("未登录或登录已过期", 1005),
    USER_INPUT_EMPTY("用户输入不能为空", 4001)
    ;

    private final String message;
    private final Integer code;
}
