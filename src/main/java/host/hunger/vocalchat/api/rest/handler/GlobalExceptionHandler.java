package host.hunger.vocalchat.api.rest.handler;


import host.hunger.vocalchat.infrastructure.ErrorEnum.ErrorEnum;
import host.hunger.vocalchat.infrastructure.exception.BaseException;
import host.hunger.vocalchat.api.rest.result.BaseResult;
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
