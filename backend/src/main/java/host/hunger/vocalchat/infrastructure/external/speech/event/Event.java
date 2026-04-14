package host.hunger.vocalchat.infrastructure.external.speech.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "event"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AnswerEvent.class, name = "answer"),
        @JsonSubTypes.Type(value = AsrDeltaEvent.class, name = "asrDelta"),
        @JsonSubTypes.Type(value = AsrFinalEvent.class, name = "asrFinal"),
        @JsonSubTypes.Type(value = ErrorEvent.class, name = "error"),
        @JsonSubTypes.Type(value = OtherEvent.class, name = "other"),
        @JsonSubTypes.Type(value = TrackEndEvent.class, name = "trackEnd"),
        @JsonSubTypes.Type(value = TrackStartEvent.class, name = "trackStart")
})
@Data
public abstract class Event {
    @JsonProperty("event")
    private String event;
}
