package host.hunger.vocalchat.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import host.hunger.vocalchat.api.websocket.event.VoiceErrorEvent;
import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketMessageSender {

    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;

    public void sendMessage(UserId userId, Object message) {
        WebSocketSession session = sessionManager.getSession(userId);
        if (session == null) {
            log.warn("Session not found for userId: {}", userId);
            return;
        }
        sendMessage(session, message);
    }

    public void sendMessage(WebSocketSession session, Object message) {
        if (session == null) return;
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
        } catch (Exception e) {
            log.error("Failed to send message:", e);
        }
    }

    public void sendErrorMessage(UserId userId, String code, String message, boolean recoverable) {
        sendMessage(userId, new VoiceErrorEvent(null, code, message, recoverable));
    }
}
