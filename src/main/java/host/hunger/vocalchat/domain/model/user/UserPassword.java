package host.hunger.vocalchat.domain.model.user;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.Getter;

//todo
@Getter
public class UserPassword extends ValueObject {
    private String password;
    public UserPassword(String password){
        this.password = password;
    }
}
