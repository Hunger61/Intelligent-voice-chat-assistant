package host.hunger.vocalchat.domain.model.aiassistant;

import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;
import host.hunger.vocalchat.domain.model.shared.AggregateRoot;
import host.hunger.vocalchat.domain.model.shared.Identity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIAssistant extends AggregateRoot<AIAssistantId> {

    private AIAssistantName name;
    private AIAssistantDescription description;
    private AIAssistantCharacter assistantCharacter;
    private UserId userId;
    private KnowledgeBaseId knowledgeBaseId;


    public AIAssistant (AIAssistantName name,
                                     AIAssistantDescription description,
                                     AIAssistantCharacter character) {
        super(Identity.generate(AIAssistantId.class));
        this.name = name;
        this.description = description;
        this.assistantCharacter = character;
    }

    public AIAssistant (AIAssistantName name,
                                     AIAssistantDescription description,
                                     AIAssistantCharacter character,
                                     KnowledgeBaseId knowledgeBaseId) {
        super(Identity.generate(AIAssistantId.class));
        this.name = name;
        this.description = description;
        this.assistantCharacter = character;
        this.knowledgeBaseId = knowledgeBaseId;
    }

    public AIAssistant(UserId userId, AIAssistantName name,
                       AIAssistantDescription description, AIAssistantCharacter character) {
        super(Identity.generate(AIAssistantId.class));
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.assistantCharacter = character;
    }

    public AIAssistant(UserId userId, AIAssistantName name,
                       AIAssistantDescription description, AIAssistantCharacter character,
                       KnowledgeBaseId knowledgeBaseId) {
        super(Identity.generate(AIAssistantId.class));
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.assistantCharacter = character;
        this.knowledgeBaseId = knowledgeBaseId;
    }

    public AIAssistant(AIAssistantId aiAssistantId, UserId userId, AIAssistantName name,
                       AIAssistantDescription description, AIAssistantCharacter character) {
        super(aiAssistantId);
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.assistantCharacter = character;
    }

    public AIAssistant(AIAssistantId aiAssistantId, UserId userId, AIAssistantName name,
                       AIAssistantDescription description, AIAssistantCharacter character,
                       KnowledgeBaseId knowledgeBaseId) {
        super(aiAssistantId);
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.assistantCharacter = character;
        this.knowledgeBaseId = knowledgeBaseId;
    }
}
