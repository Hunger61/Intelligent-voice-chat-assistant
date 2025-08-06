package host.hunger.vocalchat.domain.model.user;

import com.baomidou.mybatisplus.annotation.TableName;
import host.hunger.vocalchat.domain.model.aiassistant.AIAssistantId;
import host.hunger.vocalchat.domain.model.shared.AggregateRoot;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Getter
@TableName("user")
@NoArgsConstructor
public class User extends AggregateRoot<UserId> {

    private UserName name;
    private UserEmail email;
    private UserPassword password;
    private List<AIAssistantId> aiAssistantIds;

    public User(UserName name, UserEmail email, UserPassword password){
        this.name = name;
        this.email = email;
        this.password = password;
        this.aiAssistantIds = new ArrayList<>();
    }
}
