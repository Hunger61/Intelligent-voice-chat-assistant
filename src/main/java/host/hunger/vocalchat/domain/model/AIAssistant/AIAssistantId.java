package host.hunger.vocalchat.domain.model.AIAssistant;

import java.util.Objects;
import java.util.UUID;

public class AIAssistantId {
    private final String id;

    public AIAssistantId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        this.id = id.trim();
    }

    public static AIAssistantId generate() {
        return new AIAssistantId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AIAssistantId other = (AIAssistantId) o;
        return this.id.equals(other.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
