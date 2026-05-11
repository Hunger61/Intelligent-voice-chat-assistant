package host.hunger.vocalchat.infrastructure.external.speech.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class MetricsEvent extends Event {
    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("key")
    private String key;

    @JsonProperty("duration")
    private Long duration;

    @JsonProperty("data")
    private Map<String, Object> data;
}