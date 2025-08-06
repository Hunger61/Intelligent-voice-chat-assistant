package host.hunger.vocalchat.domain.model.user;

import host.hunger.vocalchat.domain.model.shared.ValueObject;

//todo
public class UserEmail extends ValueObject {
    private String email;
    public UserEmail(String email){
        this.email = email;
    }
}
