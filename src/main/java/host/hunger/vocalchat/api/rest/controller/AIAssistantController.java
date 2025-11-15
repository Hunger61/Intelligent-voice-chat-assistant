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
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/llm")
public class AIAssistantController {

    private final AIAssistantApplicationService aiAssistantApplicationService;


    @PostMapping("/createNewAssistant")
    public void createNewAssistant(@RequestBody AIAssistantConfigDTO aiAssistantConfigDTO) {
        aiAssistantApplicationService.createNewAssistant(aiAssistantConfigDTO.getName(), aiAssistantConfigDTO.getDescription(), aiAssistantConfigDTO.getCharacter());
    }


    @PostMapping("/generateReply")
    @SkipToken
    public String generateReply(@RequestBody QuestionDTO questionDTO) {
        return aiAssistantApplicationService.answerQuestion(questionDTO.getQuestion(), questionDTO.getAiAssistantId());
    }

    @PostMapping(value = "/streamGenerateReply", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGenerateReply(@RequestBody QuestionDTO questionDTO) {
        SseEmitter emitter = createEmitter();

        aiAssistantApplicationService.streamingAnswerQuestionAsync(
                questionDTO.getQuestion(),
                questionDTO.getAiAssistantId(),
                token -> sendSafe(emitter, SseEmitter.event().name("token").data(token)),
                () -> {
                    sendSafe(emitter, SseEmitter.event().name("done").data("complete"));
                    emitter.complete();
                },
                ex -> {
                    sendSafe(emitter, SseEmitter.event().name("error").data(ex.getMessage()));
                    emitter.completeWithError(ex);
                }
        );
        return emitter;
    }

    @GetMapping(value = "/streamGenerateReply", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGenerateReplyGet(@RequestParam("question") String question,
                                             @RequestParam("aiAssistantId") String aiAssistantId) {
        SseEmitter emitter = createEmitter();
        aiAssistantApplicationService.streamingAnswerQuestionAsync(
                question,
                aiAssistantId,
                token -> sendSafe(emitter, SseEmitter.event().name("token").data(token)),
                () -> {
                    sendSafe(emitter, SseEmitter.event().name("done").data("complete"));
                    emitter.complete();
                },
                ex -> {
                    sendSafe(emitter, SseEmitter.event().name("error").data(ex.getMessage()));
                    emitter.completeWithError(ex);
                }
        );
        return emitter;
    }

    private SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(10));
        emitter.onTimeout(() -> {
            sendSafe(emitter, SseEmitter.event().name("error").data("timeout"));
            emitter.complete();
        });
        emitter.onError(e -> log.error("SSE error", e));
        return emitter;
    }

    private void sendSafe(SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        try {
            emitter.send(event);
        } catch (IOException e) {
            log.error("Failed to send SSE", e);
        }
    }
}
