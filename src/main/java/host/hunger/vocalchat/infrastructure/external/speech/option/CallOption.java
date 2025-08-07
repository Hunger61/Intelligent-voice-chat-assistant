// infrastructure/external/speech/model/CallOption.java
package host.hunger.vocalchat.infrastructure.external.speech.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallOption {

    @JsonProperty("denoise")
    private Boolean denoise;

    @JsonProperty("codec")
    private String codec;

    @JsonProperty("offer")
    private String offer;

    @JsonProperty("callee")
    private String callee;

    @JsonProperty("caller")
    private String caller;

    @JsonProperty("asr")
    private ASROption asr;

    @JsonProperty("tts")
    private TTSOption tts;

    @JsonProperty("handshakeTimeout")
    private String handshakeTimeout;

    @JsonProperty("enableIpv6")
    private Boolean enableIPv6;

    @JsonProperty("extra")
    private Map<String, String> extra;
}
