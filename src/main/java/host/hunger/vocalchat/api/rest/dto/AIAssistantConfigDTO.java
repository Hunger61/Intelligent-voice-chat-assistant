package host.hunger.vocalchat.api.rest.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AIAssistantConfigDTO implements Serializable {
    private String name;
    private String description;
    private String character;
}
