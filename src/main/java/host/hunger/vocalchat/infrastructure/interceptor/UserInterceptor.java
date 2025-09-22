package host.hunger.vocalchat.infrastructure.interceptor;

import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.UserRepository;
import host.hunger.vocalchat.infrastructure.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {

    public static final ThreadLocal<User> userHolder = new ThreadLocal<>();

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public boolean preHandle(
            HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler)
            throws Exception {

        String token = request.getHeader("Token");

        if (token == null || token.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Missing token");
            return false;
        }

        String id = jwtUtil.resolveToken(token);
        UserId userId = new UserId(id);
        // 根据 code 字段查询用户
        User user = userRepository.findById(userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: User not found");
            return false;
        }

        // 保存用户到 ThreadLocal
        userHolder.set(user);
        return true;
    }

    @Override
    public void afterCompletion(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            Exception ex) {
        userHolder.remove();
    }
}
