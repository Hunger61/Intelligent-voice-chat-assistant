package host.hunger.vocalchat.infrastructure.aop;

import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.shared.trace.RequestTraceContext;
import host.hunger.vocalchat.shared.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Aspect
@Component
public class OperateAspect {
    @Pointcut("@annotation(host.hunger.vocalchat.api.rest.annotation.OperateLog)")
    public void operateLog() {
    }

    @Around("operateLog()")
    public Object aroundMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = proceedingJoinPoint.proceed();
            long cost = System.currentTimeMillis() - start;
            User user = UserContext.get();
            String userId = user == null || user.getId() == null ? "anonymous" : user.getId().toString();

            Optional.ofNullable(RequestTraceContext.get()).ifPresent(trace ->
                    log.info("trace userId={} spend={}ms", userId, cost));
            return result;
        } catch (Throwable ex) {
            long cost = System.currentTimeMillis() - start;
            log.error("request failed, method={}, spend={}ms", proceedingJoinPoint.getSignature().toShortString(), cost, ex);
            throw ex;
        }
    }
}
