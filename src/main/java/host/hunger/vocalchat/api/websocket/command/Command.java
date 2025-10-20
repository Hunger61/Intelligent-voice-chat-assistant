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
        @JsonSubTypes.Type(value = ConfigureLLMCommand.class, name = "start_llm"),
        @JsonSubTypes.Type(value = GenerateCommand.class, name = "generate"),
})
public abstract class Command {
    @JsonProperty("command")
    protected String command;
}
