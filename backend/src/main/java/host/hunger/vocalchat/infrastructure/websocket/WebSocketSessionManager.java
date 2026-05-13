package host.hunger.vocalchat.infrastructure.websocket;

import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketSessionManager {

    private final Map<UserId, WebSocketSession> sessionsByUserId = new ConcurrentHashMap<>();

    public void registerSession(UserId userId, WebSocketSession session) {
        if (userId == null || session == null) {
            log.warn("Invalid userId or session");
            return;
        }
        sessionsByUserId.put(userId, session);
    }

    public void unregisterSession(UserId userId) {
        sessionsByUserId.remove(userId);
    }

    public void unregisterSession(WebSocketSession session) {
        UserId userId = (UserId) session.getAttributes().get("userId");
        if (userId != null) {
            sessionsByUserId.remove(userId);
        }
    }

    public WebSocketSession getSession(UserId userId) {
        return sessionsByUserId.get(userId);
    }

    public Map<UserId, WebSocketSession> getAllSessions() {
        return new HashMap<>(sessionsByUserId);
    }
}

