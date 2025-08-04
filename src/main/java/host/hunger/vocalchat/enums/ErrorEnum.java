package host.hunger.vocalchat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorEnum {

    COMMON_ERROR("系统异常", 1000);
    private final String message;
    private final Integer code;
}
