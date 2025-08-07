package host.hunger.vocalchat.infrastructure.external.speech.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

@Data
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