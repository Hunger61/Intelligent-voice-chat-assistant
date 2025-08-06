package host.hunger.vocalchat.domain.model.user;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.Getter;

//todo
@Getter
public class UserName extends ValueObject {
    private String userName;

    public UserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (userName.length() > 50) {
            throw new IllegalArgumentException("Username cannot be longer than 50 characters");
        }
        this.userName = userName;
    }
}
