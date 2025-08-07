package host.hunger.vocalchat.infrastructure.websocket;

import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@AllArgsConstructor
public class WebSocketSessionManager {

    private final Map<UserId, WebSocketSession> sessionsByUserId = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, UserId> userIdsBySession = new ConcurrentHashMap<>();

    public void registerSession(UserId userId, WebSocketSession session) {
        if(userId == null || session == null){
            log.warn("Invalid userId or session");
            return;
        }
        sessionsByUserId.put(userId, session);
        userIdsBySession.put(session, userId);
    }

    public void unregisterSession(UserId userId) {
        WebSocketSession session = sessionsByUserId.get(userId);
        if (session != null) {
            sessionsByUserId.remove(userId);
            userIdsBySession.remove(session);
        }
    }

    public void unregisterSession(WebSocketSession session) {
        UserId userId = userIdsBySession.get(session);
        if (userId != null) {
            userIdsBySession.remove(session);
            sessionsByUserId.remove(userId);
        }
    }

    public WebSocketSession getSession(UserId userId) {
        return sessionsByUserId.get(userId);
    }

    public UserId getUserId(WebSocketSession session) {
        return userIdsBySession.get(session);
    }

    public Map<UserId, WebSocketSession> getAllSessions() {
        return new HashMap<>(sessionsByUserId);
    }
}

