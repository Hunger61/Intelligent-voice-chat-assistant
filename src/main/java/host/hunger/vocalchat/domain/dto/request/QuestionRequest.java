package host.hunger.vocalchat.domain.dto.request;

import lombok.Data;

@Data
public class QuestionRequest {
    private String question;
    private boolean enableSearch;
}
