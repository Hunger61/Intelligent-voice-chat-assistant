package host.hunger.vocalchat.domain.model.user;

import host.hunger.vocalchat.domain.model.shared.ValueObject;
import lombok.Getter;

@Getter
public class UserEmail extends ValueObject {
    private final String email;
    public UserEmail(String email){
        if (email == null || email.trim().isBlank()||!isValidEmail(email)){
            throw new IllegalArgumentException("Invalid email");
        }
        this.email = email.trim().toLowerCase();
    }

    private boolean isValidEmail(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
