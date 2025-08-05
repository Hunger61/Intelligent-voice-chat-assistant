package host.hunger.vocalchat.api.websocket.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import host.hunger.vocalchat.api.websocket.dto.Command;
import host.hunger.vocalchat.api.websocket.dto.GenerateCommand;
import host.hunger.vocalchat.api.websocket.dto.StartLLMCommand;
import host.hunger.vocalchat.application.service.QuestionAnsweringApplicationService;
import host.hunger.vocalchat.infrastructure.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FrontEndWebSocketHandler implements WebSocketHandler {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;
    private final QuestionAnsweringApplicationService questionAnsweringApplicationService;

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        webSocketSessionManager.registerSession(session.getId(), session);
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
            switch (command.getCommand()){
                case "start_llm":
                    handleStartLLMCommand(session, (StartLLMCommand)command);
                    break;
                case "generate":handleGenerateCommand(session,(GenerateCommand)command);
                    break;
                    //todo
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
    //todo
    private void handleStartLLMCommand(WebSocketSession session, StartLLMCommand command) {

    }
    //todo
    private void handleGenerateCommand(WebSocketSession session, GenerateCommand command) {
        questionAnsweringApplicationService.answerQuestion(command.getContent(), session.getId());//todo 实现从session中获取userId
    }
}
