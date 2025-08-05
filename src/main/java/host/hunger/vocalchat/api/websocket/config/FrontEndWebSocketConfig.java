package host.hunger.vocalchat.api.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import host.hunger.vocalchat.api.websocket.handler.FrontEndWebSocketHandler;
import host.hunger.vocalchat.application.service.QuestionAnsweringApplicationService;
import host.hunger.vocalchat.infrastructure.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class FrontEndWebSocketConfig implements WebSocketConfigurer {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;
    private final QuestionAnsweringApplicationService questionAnsweringApplicationService;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler(), "/ws/chat")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler chatWebSocketHandler() {
        return new FrontEndWebSocketHandler(webSocketSessionManager,objectMapper,questionAnsweringApplicationService);
    }

}
