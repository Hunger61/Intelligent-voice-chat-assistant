package host.hunger.vocalchat.domain.model.shared;

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

    public String getValue() {
        return id;
    }

    public static <T extends Identity> T generate(Class<T> identityClass) {
        try {
            String uuid = UUID.randomUUID().toString();
            return identityClass.getConstructor(String.class).newInstance(uuid);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate identity", e);
        }
    }
}
