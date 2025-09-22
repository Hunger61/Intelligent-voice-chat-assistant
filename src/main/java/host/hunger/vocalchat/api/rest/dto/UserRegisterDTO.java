package host.hunger.vocalchat.api.rest.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterDTO implements Serializable {
    private String nickName;
    private String password;
    private String email;
    private String verificationCode;
}
