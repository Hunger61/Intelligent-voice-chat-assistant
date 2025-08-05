package host.hunger.vocalchat.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendMessage(String sessionId, Object message) {
        WebSocketSession session = sessionManager.getSession(sessionId);
        if (session != null) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
            } catch (Exception e) {
                log.error("Failed to send message to session: {}", sessionId, e);
            }
        }
    }

    public void sendErrorMessage(String sessionId, String errorMessage) {
        sendMessage(sessionId, errorMessage);
    }
}
