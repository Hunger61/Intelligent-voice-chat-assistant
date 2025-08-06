package host.hunger.vocalchat.infrastructure.result;

import host.hunger.vocalchat.infrastructure.ErrorEnum.ErrorEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResult<T> implements Serializable {
    private Integer code;
    private String message;
    private boolean success;
    private T data;

    public static <T> BaseResult<T> success(T data) {
        BaseResult<T> response = new BaseResult<>();
        response.setSuccess(true);
        response.setCode(null);
        response.setMessage(null);
        response.setData(data);
        return response;
    }

    public static <T> BaseResult<T> success() {
        return success(null);
    }

    public static <T> BaseResult<T> failure(ErrorEnum errorEnum) {
        BaseResult<T> response = new BaseResult<>();
        response.setCode(errorEnum.getCode());
        response.setMessage(errorEnum.getMessage());
        response.setSuccess(false);
        response.setData(null);
        return response;
    }
}
