package host.hunger.vocalchat.api.websocket.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import host.hunger.vocalchat.api.websocket.dto.Command;
import host.hunger.vocalchat.api.websocket.dto.GenerateCommand;
import host.hunger.vocalchat.api.websocket.dto.StartLLMCommand;
import host.hunger.vocalchat.application.service.QuestionAnsweringApplicationService;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistant;
import host.hunger.vocalchat.infrastructure.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.*;

@Slf4j
@RequiredArgsConstructor
// todo 应直接获得UserId与session关联，而非从assistant中获取UserId
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
        AIAssistant assistant = questionAnsweringApplicationService.findAIAssistantById(command.getAiAssistantId());
        try {
            session.getAttributes().put("ai_assistant",assistant);
            webSocketSessionManager.registerSession(assistant.getUserId(),session);//todo 吐了🤮
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGenerateCommand(WebSocketSession session, GenerateCommand command) {
        questionAnsweringApplicationService.answerQuestion((AIAssistant) session.getAttributes().get("ai_assistant"),command.getContent());//todo 实现从session中获取userId：实现拦截器，且要改前端
    }
}
