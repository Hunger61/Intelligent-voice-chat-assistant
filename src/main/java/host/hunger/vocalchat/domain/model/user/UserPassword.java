package host.hunger.vocalchat.domain.model.user;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Getter
public class UserPassword extends ValueObject {
    private final String password;
    public UserPassword(String password){
        this.password = encrypt(password);
    }

    private String encrypt(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean matches(UserPassword rawPassword){
        return BCrypt.checkpw(rawPassword.getPassword(), this.password);
    }
}
