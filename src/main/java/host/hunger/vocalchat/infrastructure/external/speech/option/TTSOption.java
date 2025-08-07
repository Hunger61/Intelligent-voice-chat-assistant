// infrastructure/external/speech/model/TTSOption.java
package host.hunger.vocalchat.infrastructure.external.speech.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TTSOption {

    @JsonProperty("samplerate")
    private Integer samplerate; // tts samplerate, 16000|48000

    @JsonProperty("provider")
    private String provider; // tts provider, tencent|aliyun

    @JsonProperty("speed")
    private Float speed;

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("secretId")
    private String secretId;

    @JsonProperty("secretKey")
    private String secretKey;

    @JsonProperty("volume")
    private Integer volume;

    @JsonProperty("speaker")
    private String speaker;

    @JsonProperty("codec")
    private String codec;

    @JsonProperty("subtitle")
    private Boolean subtitle;

    @JsonProperty("emotion")
    private String emotion;

    @JsonProperty("endpoint")
    private String endpoint;

    @JsonProperty("extra")
    private Map<String, String> extra;
}
