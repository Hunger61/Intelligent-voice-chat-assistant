package host.hunger.vocalchat.api.rest.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionDTO implements Serializable {
    private String question;
    private String aiAssistantId;
}