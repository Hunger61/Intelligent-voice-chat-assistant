package host.hunger.vocalchat.infrastructure.external.speech.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnswerEvent extends Event {
    @JsonProperty("trackId")
    private String trackID;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("sdp")
    private String sdp;
}
