package host.hunger.vocalchat.api.websocket.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import host.hunger.vocalchat.api.websocket.command.Command;
import host.hunger.vocalchat.api.websocket.command.VoiceInterruptCommand;
import host.hunger.vocalchat.api.websocket.command.VoiceMuteCommand;
import host.hunger.vocalchat.api.websocket.command.VoiceSessionJoinCommand;
import host.hunger.vocalchat.api.websocket.command.VoiceSessionLeaveCommand;
import host.hunger.vocalchat.api.websocket.command.VoiceUnmuteCommand;
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

    //todo
    private void handleTextMessage(WebSocketSession session,TextMessage message){
        log.info("Received message: {}", message.getPayload());
        try {
            Command command = objectMapper.readValue(message.getPayload(), Command.class);
            if (command instanceof VoiceSessionJoinCommand){
                handleVoiceSessionJoinCommand(session, (VoiceSessionJoinCommand)command);
            } else if(command instanceof VoiceSessionLeaveCommand){
                handleVoiceSessionLeaveCommand(session,(VoiceSessionLeaveCommand)command);
            }else if(command instanceof VoiceInterruptCommand){
                handleVoiceInterruptCommand(session,(VoiceInterruptCommand)command);
            }else if(command instanceof VoiceMuteCommand){
                handleVoiceMuteCommand(session,(VoiceMuteCommand)command);
            }else if(command instanceof VoiceUnmuteCommand){
                handleVoiceUnmuteCommand(session,(VoiceUnmuteCommand)command);
            }else {
                log.error("Unknown command: {}", command);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

        //todo
    private void handlePingMessage(PingMessage message){

    }
    //todo
    private void handlePongMessage(PongMessage message){

    }

    private void handleVoiceUnmuteCommand(WebSocketSession session, VoiceUnmuteCommand command) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleVoiceUnmuteCommand'");
    }

    private void handleVoiceMuteCommand(WebSocketSession session, VoiceMuteCommand command) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleVoiceMuteCommand'");
    }

    private void handleVoiceInterruptCommand(WebSocketSession session, VoiceInterruptCommand command) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleVoiceInterruptCommand'");
    }

    private void handleVoiceSessionLeaveCommand(WebSocketSession session, VoiceSessionLeaveCommand command) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleVoiceSessionLeaveCommand'");
    }

    private void handleVoiceSessionJoinCommand(WebSocketSession session, VoiceSessionJoinCommand command) {
        String aiAssistantId = command.getAiAssistantId();
        if (aiAssistantId == null || aiAssistantId.trim().isEmpty()) {
            log.error("AI Assistant ID is null or empty in StartLLMCommand");
            return;
        }
        AIAssistant aiAssistant = aiAssistantApplicationService.getAIAssistantById(new AIAssistantId(aiAssistantId));
        session.getAttributes().put("aiAssistant", aiAssistant);
    }
}
