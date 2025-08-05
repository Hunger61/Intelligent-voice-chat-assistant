package host.hunger.vocalchat.api.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "command"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StartLLMCommand.class, name = "start_llm"),
        @JsonSubTypes.Type(value = GenerateCommand.class, name = "generate"),
})
@Getter
public abstract class Command {
    @JsonProperty("command")
    protected String command;
}
