package host.hunger.vocalchat.api.websocket.handler;

import host.hunger.vocalchat.api.websocket.dto.ChatRecEvent;
import host.hunger.vocalchat.domain.model.aiassistant.QuestionAnsweredEvent;
import host.hunger.vocalchat.domain.model.shared.DomainEvent;
import host.hunger.vocalchat.infrastructure.websocket.WebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DomainEventListener {

    private final WebSocketMessageSender webSocketMessageSender;

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEvent(DomainEvent event) {
        if (event instanceof QuestionAnsweredEvent questionAnsweredEvent) {
            ChatRecEvent chatRecEvent = new ChatRecEvent(
                    "chat_response",
                    "",//todo
                    questionAnsweredEvent.getAnswer()
            );
            webSocketMessageSender.sendMessage(questionAnsweredEvent.getSessionId(), chatRecEvent);
        }
    }
}
