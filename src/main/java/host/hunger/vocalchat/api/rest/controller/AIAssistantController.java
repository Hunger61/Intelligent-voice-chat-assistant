package host.hunger.vocalchat.api.rest.controller;

import host.hunger.vocalchat.api.rest.annotation.SkipToken;
import host.hunger.vocalchat.api.rest.dto.AIAssistantConfigDTO;
import host.hunger.vocalchat.api.rest.dto.QuestionDTO;
import host.hunger.vocalchat.application.service.AIAssistantApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/llm")
public class AIAssistantController {

    private final AIAssistantApplicationService aiAssistantApplicationService;
    private final ExecutorService virtualExecutor;


    @PostMapping("/createNewAssistant")
    public void createNewAssistant(@RequestBody AIAssistantConfigDTO aiAssistantConfigDTO){
        aiAssistantApplicationService.createNewAssistant(aiAssistantConfigDTO.getName(), aiAssistantConfigDTO.getDescription(), aiAssistantConfigDTO.getCharacter());
    }


    @PostMapping("/generateReply")
    @SkipToken
    public String generateReply(@RequestBody QuestionDTO questionDTO){
        return aiAssistantApplicationService.answerQuestion(questionDTO.getQuestion(), questionDTO.getAiAssistantId());
    }

    @PostMapping(value = "/streamGenerateReply", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGenerateReply(@RequestBody QuestionDTO questionDTO) {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(10));
        CompletableFuture<String> future = aiAssistantApplicationService.answerQuestionAsync(questionDTO.getQuestion(), questionDTO.getAiAssistantId());

        // send periodic heartbeat
        CompletableFuture.runAsync(() -> {
            try {
                int i = 0;
                while (!future.isDone()) {
                    emitter.send(SseEmitter.event().name("progress").data("thinking..." + (i++)));
                }
            } catch (Exception e) {
                log.warn("Heartbeat sender stopped", e);
            }
        },virtualExecutor);

        future.whenComplete((answer, ex) -> {
            try {
                if (ex != null) {
                    log.error("LLM async failed", ex);
                    emitter.send(SseEmitter.event().name("error").data("LLM error: " + ex.getMessage()));
                } else {
                    emitter.send(SseEmitter.event().name("final").data(answer));
                }
            } catch (IOException e) {
                log.error("Failed to send SSE", e);
            } finally {
                emitter.complete();
            }
        });

        return emitter;
    }
}
