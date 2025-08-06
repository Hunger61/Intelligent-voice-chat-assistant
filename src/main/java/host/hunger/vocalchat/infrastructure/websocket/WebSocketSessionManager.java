package host.hunger.vocalchat.infrastructure.websocket;

import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class WebSocketSessionManager {
    private final Map<String, WebSocketSession> sessionsById = new ConcurrentHashMap<>();
    private final Map<UserId, WebSocketSession> sessionsByUserId = new ConcurrentHashMap<>();

    public void registerSession(String sessionId, WebSocketSession session) {
        sessionsById.put(sessionId, session);
    }

    public void registerSession(UserId userId, WebSocketSession session) {
        sessionsByUserId.put(userId, session);
    }

    public void unregisterSession(String sessionId) {
        sessionsById.remove(sessionId);
    }

    public void unregisterSession(UserId userId) {
        sessionsByUserId.remove(userId);
    }

    public WebSocketSession getSession(String sessionId) {
        return sessionsById.get(sessionId);
    }

    public WebSocketSession getSession(UserId userId) {
        return sessionsByUserId.get(userId);
    }
}
