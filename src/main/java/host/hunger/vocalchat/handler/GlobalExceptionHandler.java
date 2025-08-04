package host.hunger.vocalchat.handler;


import host.hunger.vocalchat.enums.ErrorEnum;
import host.hunger.vocalchat.exception.BaseException;
import host.hunger.vocalchat.result.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public BaseResult<ErrorEnum> exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return BaseResult.failure(ex.getErrorEnum());
    }

    @ExceptionHandler(Exception.class)
    public BaseResult<ErrorEnum> handleOtherException(Exception ex) {
        log.error("未知异常：", ex);
        return BaseResult.failure(ErrorEnum.COMMON_ERROR);
    }
}
