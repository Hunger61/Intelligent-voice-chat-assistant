package host.hunger.vocalchat.api.websocket.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "command"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VoiceSessionJoinCommand.class, name = "voice.session.join"),
        @JsonSubTypes.Type(value = VoiceSessionLeaveCommand.class, name = "voice.session.leave"),
        @JsonSubTypes.Type(value = VoiceInterruptCommand.class, name = "voice.interrupt"),
        @JsonSubTypes.Type(value = VoiceMuteCommand.class, name = "voice.mute"),
        @JsonSubTypes.Type(value = VoiceUnmuteCommand.class, name = "voice.unmute"),
})
public abstract class Command {
    @JsonProperty("command")
    protected String command;

    @JsonProperty("seq")
    protected Long seq;
}
