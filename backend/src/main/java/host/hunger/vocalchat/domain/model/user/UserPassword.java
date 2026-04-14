package host.hunger.vocalchat.domain.model.user;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Getter
public class UserPassword extends ValueObject {
    private final String password;
    public UserPassword(String password){
        if (password == null || password.isBlank()){
            throw new IllegalArgumentException("password must not be null or blank");
        }
        if ((password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) && password.length() == 60){
            this.password = password;
        }
        else{
            this.password = encrypt(password);
        }
    }

    private String encrypt(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean matches(String rawPassword){
        return BCrypt.checkpw(rawPassword, this.password);
    }
}
