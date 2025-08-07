// infrastructure/external/speech/model/ASROption.java
package host.hunger.vocalchat.infrastructure.external.speech.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ASROption {

    @JsonProperty("provider")
    private String provider; // asr provider, tencent|aliyun

    @JsonProperty("model")
    private String model;

    @JsonProperty("language")
    private String language;

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("secretId")
    private String secretId;

    @JsonProperty("secretKey")
    private String secretKey;

    @JsonProperty("modelType")
    private String modelType;

    @JsonProperty("bufferSize")
    private Integer bufferSize;

    @JsonProperty("sampleRate")
    private Long sampleRate;

    @JsonProperty("endpoint")
    private String endpoint;

    @JsonProperty("extra")
    private Map<String, String> extra;
}
