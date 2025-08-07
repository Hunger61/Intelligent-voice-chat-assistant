package host.hunger.vocalchat.api.websocket.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import host.hunger.vocalchat.api.websocket.command.Command;
import host.hunger.vocalchat.api.websocket.command.GenerateCommand;
import host.hunger.vocalchat.api.websocket.command.StartLLMCommand;
import host.hunger.vocalchat.application.service.AIAssistantApplicationService;
import host.hunger.vocalchat.application.service.QuestionAnsweringApplicationService;
import host.hunger.vocalchat.domain.event.DomainEventPublisher;
import host.hunger.vocalchat.domain.event.QuestionAnsweredEvent;
import host.hunger.vocalchat.domain.event.QuestionReceivedEvent;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.infrastructure.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.*;

@Slf4j
@RequiredArgsConstructor
public class FrontEndWebSocketHandler implements WebSocketHandler {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;
    private final DomainEventPublisher domainEventPublisher;
    private final QuestionAnsweringApplicationService questionAnsweringApplicationService;

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        webSocketSessionManager.registerSession((UserId) session.getAttributes().get("userId"), session);
        log.info("New connection established: {}", session.getId());
    }

    @Override
    public void handleMessage(@NotNull WebSocketSession session, @NotNull WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage){
            handleTextMessage(session, (TextMessage) message);
        } else if (message instanceof PingMessage) {
            handlePingMessage((PingMessage) message);
        } else if (message instanceof PongMessage) {
            handlePongMessage((PongMessage) message);
        }
    }

    //todo
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }
    //todo
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

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
            if (command instanceof StartLLMCommand){
                handleStartLLMCommand(session, (StartLLMCommand)command);
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

    private void handleStartLLMCommand(WebSocketSession session, StartLLMCommand command) {
        String aiAssistantId = command.getAiAssistantId();
        if (aiAssistantId == null || aiAssistantId.trim().isEmpty()) {
            log.error("AI Assistant ID is null or empty in StartLLMCommand");
            return;
        }
        questionAnsweringApplicationService.setAIAssistant(new AIAssistantId(aiAssistantId));
    }

    private void handleGenerateCommand(WebSocketSession session, GenerateCommand command) {
        domainEventPublisher.publish(new QuestionReceivedEvent(command.getContent(), (UserId) session.getAttributes().get("userId")));
    }
}
