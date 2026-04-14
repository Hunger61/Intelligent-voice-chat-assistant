package host.hunger.vocalchat.api.rest.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIAssistantVO implements Serializable {
    private String id;
    private String name;
    private String description;
    private String character;
}
