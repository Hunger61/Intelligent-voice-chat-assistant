package host.hunger.vocalchat.infrastructure.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QwenLlmConfig {
    @Value("${qwen.model-name}")
    private String modelName;

    @Value("${qwen.api-key}")
    private String apiKey;

    @Value("${qwen.api-multiple-url}")
    private String apiMultipleUrl;

    @Bean
    public QwenChatModel qwenChatModel() {
        return QwenChatModel.builder()
                .modelName(modelName)
                .baseUrl(apiMultipleUrl)
                .isMultimodalModel(true)
                .apiKey(apiKey)
                .build();
    }

    @Bean
    public QwenStreamingChatModel qwenStreamingChatModel() {
        return QwenStreamingChatModel.builder()
                .modelName(modelName)
                .isMultimodalModel(true)
                .baseUrl(apiMultipleUrl)
                .apiKey(apiKey)
                .build();
    }
}
