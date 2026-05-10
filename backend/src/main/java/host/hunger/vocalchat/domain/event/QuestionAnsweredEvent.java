package host.hunger.vocalchat.domain.event;

import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.user.UserId;
import lombok.Getter;

@Getter
public class QuestionAnsweredEvent extends DomainEvent {

    private final AIAssistantId aiAssistantId;
    private final String question;
    private final String answer;
    private final UserId userId;

    public QuestionAnsweredEvent(AIAssistantId aiAssistantId, String question, String answer, UserId userId) {
        super(aiAssistantId.toString() + "QuestionAnsweredEvent");//todo
        this.aiAssistantId = aiAssistantId;
        this.question = question;
        this.answer = answer;
        this.userId = userId;
    }
}
