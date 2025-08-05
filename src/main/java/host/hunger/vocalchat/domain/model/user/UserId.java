package host.hunger.vocalchat.domain.model.user;

public class UserId {
    private final String id;

    public UserId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        this.id = id.trim();
    }
    public String getValue() {
        return id;
    }
    public static UserId generate() {
        return new UserId(java.util.UUID.randomUUID().toString());
    }
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserId other = (UserId) o;
        return this.id.equals(other.id);
    }
}
