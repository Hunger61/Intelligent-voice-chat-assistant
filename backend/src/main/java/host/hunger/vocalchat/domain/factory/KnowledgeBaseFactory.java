package host.hunger.vocalchat.domain.factory;

import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBase;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseDescription;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseName;
import host.hunger.vocalchat.domain.model.user.UserId;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeBaseFactory {

    public static KnowledgeBase createNewKnowledgeBase(UserId userId, KnowledgeBaseName name,
                                                        KnowledgeBaseDescription description) {
        return new KnowledgeBase(userId, name, description);
    }
}
