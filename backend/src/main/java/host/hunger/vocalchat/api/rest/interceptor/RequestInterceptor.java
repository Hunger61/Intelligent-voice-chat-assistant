package host.hunger.vocalchat.api.rest.interceptor;

import host.hunger.vocalchat.shared.trace.RequestTraceContext;
import host.hunger.vocalchat.shared.trace.TraceLog;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestInterceptor implements HandlerInterceptor {
    /**
     * 每次收到请求时，记录该次请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull Object handler) {
        MDC.put("TRACE_ID", UUID.randomUUID().toString());
        TraceLog preTraceLog = new TraceLog(String.valueOf(System.currentTimeMillis()), request.getRequestURI());
        RequestTraceContext.set(preTraceLog);
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull Object handler, @Nullable Exception ex) {
        RequestTraceContext.clear();
        MDC.clear();
    }

}
