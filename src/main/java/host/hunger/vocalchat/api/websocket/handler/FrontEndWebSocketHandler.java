package host.hunger.vocalchat.api.websocket.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import host.hunger.vocalchat.api.websocket.command.Command;
import host.hunger.vocalchat.api.websocket.command.ConfigureLLMCommand;
import host.hunger.vocalchat.api.websocket.command.GenerateCommand;
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
            if (command instanceof ConfigureLLMCommand){
                handleConfigureLLMCommand(session, (ConfigureLLMCommand)command);
            } else if(command instanceof GenerateCommand){
                handleGenerateCommand(session,(GenerateCommand)command);
            }else {
                log.error("Unknown command: {}", command);
            }
            //todo
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

    private void handleConfigureLLMCommand(WebSocketSession session, ConfigureLLMCommand command) {
        String aiAssistantId = command.getAiAssistantId();
        if (aiAssistantId == null || aiAssistantId.trim().isEmpty()) {
            log.error("AI Assistant ID is null or empty in StartLLMCommand");
            return;
        }
        AIAssistant aiAssistant = aiAssistantApplicationService.getAIAssistantById(new AIAssistantId(aiAssistantId));
        session.getAttributes().put("aiAssistant", aiAssistant);
    }

    private void handleGenerateCommand(WebSocketSession session, GenerateCommand command) {
        AIAssistant aiAssistant = (AIAssistant)session.getAttributes().get("aiAssistant");
        if (aiAssistant == null) {
            log.error("AI Assistant is null in GenerateCommand");
            return;
        }
//        aiAssistantApplicationService.answerQuestion(aiAssistant,command.getContent());//todo
    }
}
