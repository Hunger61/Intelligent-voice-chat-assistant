package host.hunger.vocalchat.api.rest.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AddMessagesDTO implements Serializable {
    private List<List<String>> messages;
}
