package host.hunger.vocalchat.domain.model.aiassistant;

import host.hunger.vocalchat.domain.model.shared.DomainEvent;
import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class QuestionAnsweredEvent extends DomainEvent {

    public QuestionAnsweredEvent(AIAssistantId aiAssistantId, String question, String answer, UserId userId) {
        super(UUID.randomUUID().toString(),LocalDateTime.now());
        this.aiAssistantId = aiAssistantId;
        this.question = question;
        this.answer = answer;
        this.userId = userId;
    }

    private final AIAssistantId aiAssistantId;
    private final String question;
    private final String answer;
    private final UserId userId;
}
