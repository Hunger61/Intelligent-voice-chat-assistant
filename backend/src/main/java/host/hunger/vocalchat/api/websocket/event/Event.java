package host.hunger.vocalchat.api.websocket.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "event"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VoiceSessionStartedEvent.class, name = "voice.session.started"),
        @JsonSubTypes.Type(value = VoiceSessionEndedEvent.class, name = "voice.session.ended"),
        @JsonSubTypes.Type(value = VoiceAsrInterimEvent.class, name = "voice.asr.interim"),
        @JsonSubTypes.Type(value = VoiceAsrFinalEvent.class, name = "voice.asr.final"),
        @JsonSubTypes.Type(value = VoiceLlmTokenEvent.class, name = "voice.llm.token"),
        @JsonSubTypes.Type(value = VoiceLlmThinkingEvent.class, name = "voice.llm.thinking"),
        @JsonSubTypes.Type(value = VoiceLlmCompleteEvent.class, name = "voice.llm.complete"),
        @JsonSubTypes.Type(value = VoiceTtsStartedEvent.class, name = "voice.tts.started"),
        @JsonSubTypes.Type(value = VoiceTtsEndedEvent.class, name = "voice.tts.ended"),
        @JsonSubTypes.Type(value = VoiceTtsInterruptedEvent.class, name = "voice.tts.interrupted"),
        @JsonSubTypes.Type(value = VoicePipelineStateEvent.class, name = "voice.pipeline.state"),
        @JsonSubTypes.Type(value = VoiceSpeakerChangedEvent.class, name = "voice.speaker.changed"),
        @JsonSubTypes.Type(value = VoiceErrorEvent.class, name = "voice.error"),
})
public abstract class Event {
    @JsonProperty("event")
    protected String event;

    @JsonProperty("seq")
    protected Long seq;

    protected Event() {}

    protected Event(String event, Long seq) {
        this.event = event;
        this.seq = seq;
    }
}
