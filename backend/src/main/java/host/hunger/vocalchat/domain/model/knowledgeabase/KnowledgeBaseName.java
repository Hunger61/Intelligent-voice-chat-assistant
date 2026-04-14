package host.hunger.vocalchat.domain.model.knowledgeabase;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.Getter;

@Getter
public class KnowledgeBaseName extends ValueObject{
    private final String name;

    public KnowledgeBaseName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("KnowledgeBaseName cannot be null or empty");
        }
        this.name = name;
    }
}
