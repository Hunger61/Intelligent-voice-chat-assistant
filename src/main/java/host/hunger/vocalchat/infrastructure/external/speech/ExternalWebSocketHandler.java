package host.hunger.vocalchat.infrastructure.external.speech;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import host.hunger.vocalchat.domain.event.QuestionReceivedEvent;
import host.hunger.vocalchat.domain.event.DomainEventPublisher;
import host.hunger.vocalchat.infrastructure.external.speech.event.*;
import host.hunger.vocalchat.infrastructure.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Component
@RequiredArgsConstructor
@Slf4j
//todo
public class ExternalWebSocketHandler implements WebSocketHandler {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;
    private final DomainEventPublisher domainEventPublisher;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage){
            handleTextMessage(session, (TextMessage) message);
        } else if (message instanceof PingMessage) {
            handlePingMessage((PingMessage) message);
        } else if (message instanceof PongMessage) {
            handlePongMessage((PongMessage) message);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void handlePongMessage(PongMessage message) {
    }

    private void handlePingMessage(PingMessage message) {

    }

    private void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("Received external message: {}", message.getPayload());
        try {
            Event event = objectMapper.readValue(message.getPayload(),Event.class);
            if(event instanceof AnswerEvent){
                handleAnswerEvent(session, (AnswerEvent) event);
            }
            else if(event instanceof AsrDeltaEvent){
                handleAsrDeltaEvent(session, (AsrDeltaEvent) event);
            }
            else if(event instanceof OtherEvent){
                handleOtherEvent(session, (OtherEvent) event);
            }
            else if(event instanceof ErrorEvent){
                handleErrorEvent(session, (ErrorEvent) event);
            }
            else if(event instanceof TrackStartEvent){
                handleTrackStartEvent(session, (TrackStartEvent) event);
            }
            else if(event instanceof TrackEndEvent){
                handleTrackEndEvent(session, (TrackEndEvent) event);
            }
            else if(event instanceof AsrFinalEvent){
                handleAsrFinalEvent(session, (AsrFinalEvent) event);
            }
            else{
                log.error("Unknown event type: {}", event.getClass().getName());
            }
        }
        catch (JsonProcessingException e){
            log.error("Failed to parse message: {}", message.getPayload(), e);
        }
    }

    private void handleAsrFinalEvent(WebSocketSession session, AsrFinalEvent event) {
        if (event.getText() != null && !event.getText().isBlank()) {
            domainEventPublisher.publish(new QuestionReceivedEvent(event.getText(),webSocketSessionManager.getUserId(session)));
        }
    }

    private void handleTrackEndEvent(WebSocketSession session, TrackEndEvent event) {

    }

    private void handleTrackStartEvent(WebSocketSession session, TrackStartEvent event) {

    }

    private void handleErrorEvent(WebSocketSession session, ErrorEvent event) {

    }

    private void handleOtherEvent(WebSocketSession session, OtherEvent event) {

    }

    private void handleAsrDeltaEvent(WebSocketSession session, AsrDeltaEvent event) {

    }

    private void handleAnswerEvent(WebSocketSession session, AnswerEvent event) {
    }
}
