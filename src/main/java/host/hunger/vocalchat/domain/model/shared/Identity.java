package host.hunger.vocalchat.domain.model.shared;

import java.util.Objects;
import java.util.UUID;

public abstract class Identity extends ValueObject{
    protected final String id;

    protected Identity(String id) {
        this.id = validateId(id);
    }

    private String validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        return id.trim();
    }

    public static <T extends Identity> T generate(Class<T> identityClass) {
        try {
            String uuid = UUID.randomUUID().toString();
            return identityClass.getConstructor(String.class).newInstance(uuid);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate identity", e);
        }
    }

    @Override
    public String toString() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identity identity = (Identity) o;
        return Objects.equals(id, identity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
