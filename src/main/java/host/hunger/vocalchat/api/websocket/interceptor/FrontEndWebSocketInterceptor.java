package host.hunger.vocalchat.api.websocket.interceptor;

import host.hunger.vocalchat.domain.model.user.User;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.repository.UserRepository;
import host.hunger.vocalchat.infrastructure.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class FrontEndWebSocketInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) throws Exception {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String token = servletRequest.getServletRequest().getParameter("token");
            if (token == null || token.isBlank()) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
            String id = jwtUtil.resolveToken(token);
            UserId userId = new UserId(id);
            // 根据 code 字段查询用户
            Optional<User> userOptional = userRepository.findById(userId);//todo 改用redis缓存
            if (userOptional.isEmpty()) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
            attributes.put(userId.toString(), userOptional.get());
            return true;
    }//todo

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, Exception exception) {

    }
}
