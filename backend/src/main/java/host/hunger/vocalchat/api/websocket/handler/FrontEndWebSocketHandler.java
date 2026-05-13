package host.hunger.vocalchat.api.websocket.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import host.hunger.vocalchat.api.websocket.command.Command;
import host.hunger.vocalchat.api.websocket.command.VoiceInterruptCommand;
import host.hunger.vocalchat.api.websocket.command.VoiceMuteCommand;
import host.hunger.vocalchat.api.websocket.command.VoiceSessionJoinCommand;
import host.hunger.vocalchat.api.websocket.command.VoiceSessionLeaveCommand;
import host.hunger.vocalchat.api.websocket.command.VoiceUnmuteCommand;
import host.hunger.vocalchat.api.websocket.event.VoiceChatException;
import host.hunger.vocalchat.application.service.AIAssistantApplicationService;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.infrastructure.websocket.WebSocketSessionManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.*;

@Slf4j
@AllArgsConstructor
public class FrontEndWebSocketHandler implements WebSocketHandler {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;
    private final AIAssistantApplicationService aiAssistantApplicationService;

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        webSocketSessionManager.registerSession((UserId) session.getAttributes().get("userId"), session);//todo
        log.info("New connection established: {}", session.getId());
    }

    @Override
    public void handleMessage(@NotNull WebSocketSession session, @NotNull WebSocketMessage<?> message) throws Exception {
        switch (message) {
            case TextMessage textMessage -> handleTextMessage(session, textMessage);
            case PingMessage pingMessage -> handlePingMessage(pingMessage);
            case PongMessage pongMessage -> handlePongMessage(pongMessage);
            default -> {
            }
        }
    }

    //todo
    @Override
    public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) throws Exception {

    }
    //todo
    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus closeStatus) throws Exception {

    }
    //todo
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("Received message: {}", message.getPayload());
        try {
            Command command = objectMapper.readValue(message.getPayload(), Command.class);
            if (command instanceof VoiceSessionJoinCommand) {
                handleVoiceSessionJoinCommand(session, (VoiceSessionJoinCommand) command);
            } else if (command instanceof VoiceSessionLeaveCommand) {
                handleVoiceSessionLeaveCommand(session, (VoiceSessionLeaveCommand) command);
            } else if (command instanceof VoiceInterruptCommand) {
                handleVoiceInterruptCommand(session, (VoiceInterruptCommand) command);
            } else if (command instanceof VoiceMuteCommand) {
                handleVoiceMuteCommand(session, (VoiceMuteCommand) command);
            } else if (command instanceof VoiceUnmuteCommand) {
                handleVoiceUnmuteCommand(session, (VoiceUnmuteCommand) command);
            } else {
                throw new VoiceChatException("UNKNOWN_COMMAND", "未知命令", true);
            }
        } catch (VoiceChatException e) {
            log.warn("Business error: [{}] {}", e.getCode(), e.getMessage());
            sendEvent(session, e.toEvent());
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse message: {}", message.getPayload(), e);
            sendEvent(session, new VoiceChatException("INVALID_JSON", "消息格式错误", true).toEvent());
        } catch (Exception e) {
            log.error("Error processing command", e);
            sendEvent(session, new VoiceChatException("INTERNAL_ERROR", "服务内部错误", false).toEvent());
        }
    }

    private void handlePingMessage(PingMessage message) {
    }

    private void handlePongMessage(PongMessage message) {
    }

    private void sendEvent(WebSocketSession session, Object event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            log.error("Failed to send event", e);
        }
    }

    private void handleVoiceUnmuteCommand(WebSocketSession session, VoiceUnmuteCommand command) {
        throw new VoiceChatException("NOT_IMPLEMENTED", "取消静音功能待实现", true);
    }

    private void handleVoiceMuteCommand(WebSocketSession session, VoiceMuteCommand command) {
        throw new VoiceChatException("NOT_IMPLEMENTED", "静音功能待实现", true);
    }

    private void handleVoiceInterruptCommand(WebSocketSession session, VoiceInterruptCommand command) {
        throw new VoiceChatException("NOT_IMPLEMENTED", "打断功能待实现", true);
    }

    private void handleVoiceSessionLeaveCommand(WebSocketSession session, VoiceSessionLeaveCommand command) {
        throw new VoiceChatException("NOT_IMPLEMENTED", "离开会话功能待实现", true);
    }

    private void handleVoiceSessionJoinCommand(WebSocketSession session, VoiceSessionJoinCommand command) {
        AIAssistant aiAssistant = aiAssistantApplicationService.getAIAssistantById(new AIAssistantId(command.getAiAssistantId()));
        session.getAttributes().put("aiAssistant", aiAssistant);
    }
}
