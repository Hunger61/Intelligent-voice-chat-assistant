package host.hunger.vocalchat.infrastructure.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component

public class WebSocketSessionManager {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    public void registerSession(String sessionId, WebSocketSession session) {
        sessions.put(sessionId, session);
    }

    public void unregisterSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public WebSocketSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
}
