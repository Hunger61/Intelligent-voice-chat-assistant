package host.hunger.vocalchat.api.rest.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AIAssistantConfigDTO implements Serializable {
    private String name;
    private String description;
    private String character;
    private String knowledgeBaseId;//todo 不一定需要id，可能是名字
}
