package host.hunger.vocalchat.infrastructure.exception;

import host.hunger.vocalchat.infrastructure.Enum.ErrorEnum;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final ErrorEnum errorEnum;

    public BaseException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorEnum = errorEnum;
    }
}