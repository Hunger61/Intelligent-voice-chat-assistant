package host.hunger.vocalchat.infrastructure.external.speech.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TrackStartEvent extends Event {
    @JsonProperty("trackId")
    private String trackID;

    @JsonProperty("timestamp")
    private Long timestamp;
}