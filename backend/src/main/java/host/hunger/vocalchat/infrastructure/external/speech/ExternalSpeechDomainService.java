package host.hunger.vocalchat.infrastructure.external.speech;

import host.hunger.vocalchat.domain.factory.SpeechProcessorFactory;
import host.hunger.vocalchat.domain.model.speech.Session;
import host.hunger.vocalchat.domain.model.speech.SpeechProcessor;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.domain.service.SpeechDomainService;
import host.hunger.vocalchat.infrastructure.external.speech.command.TtsCommand;
import host.hunger.vocalchat.infrastructure.websocket.WebSocketMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalSpeechDomainService implements SpeechDomainService {

    private final ExternalWebSocketHandler externalWebSocketHandler;
    private final WebSocketMessageSender webSocketMessageSender;

    @Override
    public SpeechProcessor registerSpeechProcessor(UserId userId) {
        CompletableFuture<WebSocketSession> future = connectToExternalService("ws://175.27.250.177:8080/call/webrtc?id=session123&dump=true");
        SpeechProcessor speechProcessor = SpeechProcessorFactory.create(userId);
        future.thenAccept(session -> {
            speechProcessor.setSession(new Session(session));
        }).exceptionally(throwable -> {
            log.error("Error connecting to external service: {}", throwable.getMessage());
            return null;
        });
        return speechProcessor;
    }

    @Override
    public void unregisterSpeechProcessor(SpeechProcessor speechProcessor) {
    }

    @Override
    public void TextToSpeech(String text, SpeechProcessor speechProcessor) {
        webSocketMessageSender.sendMessage((WebSocketSession) speechProcessor.getSession().getSession(),new TtsCommand(
                 text//todo
        ));
    }

    private CompletableFuture<WebSocketSession> connectToExternalService(String uri) {
        CompletableFuture<WebSocketSession> future = new CompletableFuture<>();
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        try {
            standardWebSocketClient.execute(externalWebSocketHandler,uri,future);
            return future;
        } catch (Exception e) {
            future.completeExceptionally(e);
            return future;
        }
    }
}
