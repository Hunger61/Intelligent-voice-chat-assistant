package host.hunger.vocalchat.infrastructure.config;


import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOConfig {
    @Value("${spring.minio.endpoint:${minio.endpoint}}")
    private String endpoint;
    @Value("${spring.minio.access-key:${minio.access-key}}")
    private String accessKey;
    @Value("${spring.minio.secret-key:${minio.secret-key}}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
