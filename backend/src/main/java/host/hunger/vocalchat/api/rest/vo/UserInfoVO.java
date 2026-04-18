package host.hunger.vocalchat.api.rest.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO implements Serializable {
	private String id;
	private String nickName;
	private String email;
}
