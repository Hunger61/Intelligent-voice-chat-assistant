package host.hunger.vocalchat.api.rest.aop;

import host.hunger.vocalchat.api.rest.annotation.OperateLog;
import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.shared.context.UserContext;
import host.hunger.vocalchat.shared.trace.RequestTraceContext;
import host.hunger.vocalchat.shared.trace.TraceLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class OperateAspect {
    @Pointcut("@annotation(host.hunger.vocalchat.api.rest.annotation.OperateLog)")
    public void operateLog() {
    }

    @Around("operateLog()&&@annotation(logAnno)")
    public Object aroundMethod(ProceedingJoinPoint proceedingJoinPoint, OperateLog logAnno) throws Throwable {
        long start = System.currentTimeMillis();
        User user = UserContext.get();
        try {
            Object result = proceedingJoinPoint.proceed();
            writeTrace(logAnno.value(), proceedingJoinPoint.getArgs(), result, user, start, true);
            return result;
        } catch (Throwable ex) {
            writeTrace(logAnno.value(), proceedingJoinPoint.getArgs(), ex, user, start, false);
            throw ex;
        }
    }

    private void writeTrace(String description, Object[] args, Object result, User user, long start, boolean success) {
        long cost = System.currentTimeMillis() - start;
        TraceLog traceLog = RequestTraceContext.get();
        String userId = user == null || user.getId() == null ? "anonymous" : user.getId().toString();

        if (traceLog == null) {
            if (success) {
                log.info("operateLog description={} userId={} spend={}ms", description, userId, cost);
            } else {
                log.error("operateLog failed description={} userId={} spend={}ms", description, userId, cost);
            }
            return;
        }

        traceLog.setSpendTime(cost + "ms")
                .setDescription(description)
                .setParams(Arrays.toString(args))
                .setResult(result)
                .setUser(user);
        log.info(traceLog.toLogFormat(success));
    }
}
