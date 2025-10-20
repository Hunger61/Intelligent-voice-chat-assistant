package host.hunger.vocalchat.api.websocket.event;

import host.hunger.vocalchat.domain.event.QuestionReceivedEvent;
import host.hunger.vocalchat.domain.event.QuestionAnsweredEvent;
import host.hunger.vocalchat.domain.event.DomainEvent;
import host.hunger.vocalchat.domain.repository.AIAssistantRepository;
import host.hunger.vocalchat.infrastructure.websocket.WebSocketMessageSender;
import host.hunger.vocalchat.infrastructure.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventListener {//todo

    private final WebSocketMessageSender webSocketMessageSender;

    @EventListener
    @Async
    public void handleEvent(DomainEvent event) {
        if (event instanceof QuestionAnsweredEvent questionAnsweredEvent) {
            AnswerEvent answerEvent = new AnswerEvent(
                    "chat_response",
                    "1",//todo
                    questionAnsweredEvent.getAnswer()
            );
            webSocketMessageSender.sendMessage(questionAnsweredEvent.getUserId(), answerEvent);
        }else if (event instanceof QuestionReceivedEvent questionReceivedEvent) {
            ASREvent asrEvent = new ASREvent(
                    "repeat",
                    questionReceivedEvent.getQuestion()
            );
            webSocketMessageSender.sendMessage(questionReceivedEvent.getUserId(),asrEvent);//todo UserId
        }
    }

}
