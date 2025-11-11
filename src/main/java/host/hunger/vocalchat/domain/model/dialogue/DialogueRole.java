package host.hunger.vocalchat.domain.model.dialogue;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import host.hunger.vocalchat.infrastructure.Enum.DialogueRoles;
import lombok.Getter;

import java.util.Locale;
import java.util.Objects;

@Getter
public class DialogueRole extends ValueObject {
    private final DialogueRoles role;

    public DialogueRole(DialogueRoles role) {
        if (role == null) {
            throw new IllegalArgumentException("role cannot be null");
        }
        this.role = role;
    }

    public static DialogueRole of(DialogueRoles role) {
        return new DialogueRole(role);
    }

    public static DialogueRole from(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("roleName cannot be null or empty");
        }
        try {
            DialogueRoles r = DialogueRoles.valueOf(roleName.trim().toUpperCase(Locale.ROOT));
            return new DialogueRole(r);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role name: " + roleName + ". Allowed values: " + allowedValues(), ex);
        }
    }

    private static String allowedValues() {
        StringBuilder sb = new StringBuilder();
        for (DialogueRoles dr : DialogueRoles.values()) {
            if (!sb.isEmpty()) sb.append(",");
            sb.append(dr.name());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DialogueRole that = (DialogueRole) o;
        return role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(role);
    }

    @Override
    public String toString() {
        return "DialogueRole{" +
                "role=" + role +
                '}';
    }
}
