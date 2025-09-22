package host.hunger.vocalchat.domain.model.user;

import host.hunger.vocalchat.domain.model.shared.AggregateRoot;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class User extends AggregateRoot<UserId> {

    private NickName nickName;
    private UserEmail email;
    private UserPassword password;

    public User(NickName nickName, UserEmail email, UserPassword password){
        this.nickName = nickName;
        this.email = email;
        this.password = password;
    }

    public void changeNickName(NickName nickName){
        this.nickName = nickName;
    }//todo

    public void changePassword(UserPassword password){
        this.password = password;
    }//todo

    public void changeEmail(UserEmail email){
        this.email = email;
    }//todo

    public boolean verifyPassword(UserPassword password){
        return this.password.matches(password);
    }//todo
}
