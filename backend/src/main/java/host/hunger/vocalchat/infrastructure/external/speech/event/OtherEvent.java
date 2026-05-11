package host.hunger.vocalchat.infrastructure.external.speech.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class OtherEvent extends Event {
    @JsonProperty("trackId")
    private String trackID;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("sender")
    private String sender;

    @JsonProperty("extra")
    private Map<String, String> extra;
}
