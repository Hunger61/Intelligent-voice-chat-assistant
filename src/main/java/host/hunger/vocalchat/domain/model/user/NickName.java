package host.hunger.vocalchat.domain.model.user;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.Getter;

//todo
@Getter
public class NickName extends ValueObject {
    private final String nickName;

    public NickName(String nickName) {
        if (nickName == null || nickName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nickname cannot be null or empty");
        }
        if (nickName.length() > 50) {
            throw new IllegalArgumentException("Nickname cannot be longer than 50 characters");
        }
        this.nickName = nickName;
    }
}
