package host.hunger.vocalchat.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionRequest {
    private String question;
    private boolean enableSearch;
}
