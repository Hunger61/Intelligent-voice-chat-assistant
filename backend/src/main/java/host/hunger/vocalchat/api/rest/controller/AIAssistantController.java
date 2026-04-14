package host.hunger.vocalchat.api.rest.controller;

import ai.djl.util.Pair;
import host.hunger.vocalchat.api.rest.annotation.AutoResult;
import host.hunger.vocalchat.api.rest.annotation.SkipToken;
import host.hunger.vocalchat.api.rest.dto.AIAssistantConfigDTO;
import host.hunger.vocalchat.api.rest.dto.QuestionDTO;
import host.hunger.vocalchat.api.rest.vo.AIAssistantVO;
import host.hunger.vocalchat.application.service.AIAssistantApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public/aiAssistant")
public class AIAssistantController {

    private final AIAssistantApplicationService aiAssistantApplicationService;
    private final ScheduledExecutorService keepAliveScheduler;


    @PostMapping("/createNewAssistant")
    public void createNewAssistant(@RequestBody AIAssistantConfigDTO aiAssistantConfigDTO) {
        aiAssistantApplicationService.createNewAssistant(aiAssistantConfigDTO.getName(), aiAssistantConfigDTO.getDescription(), aiAssistantConfigDTO.getCharacter());
    }

    //弃用
    @PostMapping("/generateReply")
    @SkipToken
    @AutoResult
    public String generateReply(@RequestBody QuestionDTO questionDTO) {
        return aiAssistantApplicationService.answerQuestion(questionDTO.getQuestion(), questionDTO.getAiAssistantId());
    }

    @PostMapping(value = "/streamGenerateReply", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SkipToken
    public SseEmitter streamGenerateReply(@RequestBody QuestionDTO questionDTO) {
        log.info("[SSE] POST /streamGenerateReply received: aiAssistantId={}, questionLength={}", questionDTO.getAiAssistantId(), questionDTO.getQuestion() == null ? 0 : questionDTO.getQuestion().length());
        return startStreaming(questionDTO.getQuestion(), questionDTO.getAiAssistantId());
    }

    private SseEmitter startStreaming(String question, String aiAssistantId) {
        SseEmitter emitter = createEmitter();
        try {
            log.debug("[SSE] invoking application service streamingAnswerQuestionAsync (POST)");
            sendSafe(emitter, SseEmitter.event().name("started").data("invoked"));

            aiAssistantApplicationService.streamingAnswerQuestionAsync(
                    question,
                    aiAssistantId,
                    token -> {
                        try {
                            log.debug("[SSE] token callback invoked (POST), tokenLength={}", token == null ? 0 : token.length());
                            if ("__FALLBACK_START__".equals(token)) {
                                sendSafe(emitter, SseEmitter.event().name("fallback").data("true"));
                            } else {
                                sendSafe(emitter, SseEmitter.event().name("token").data(token == null ? "" : token));
                            }
                        } catch (Exception e) {
                            log.error("[SSE] exception inside token callback (POST)", e);
                        }
                    },
                    () -> {
                        try {
                            log.info("[SSE] completed callback invoked (POST)");
                            sendSafe(emitter, SseEmitter.event().name("done").data("complete"));
                        } catch (Exception e) {
                            log.error("[SSE] exception inside completed callback (POST)", e);
                        } finally {
                            emitter.complete();
                        }
                    },
                    ex -> {
                        try {
                            log.error("[SSE] error callback invoked (POST)", ex);
                            sendSafe(emitter, SseEmitter.event().name("error").data(String.valueOf(ex)));
                        } catch (Exception e) {
                            log.error("[SSE] exception inside error callback (POST)", e);
                        } finally {
                            // 安全完成，不触发全局异常处理器在 SSE 流上返回 JSON
                            try {
                                emitter.complete();
                            } catch (Exception ignore) {
                            }
                        }
                    }
            );

            log.info("[SSE] streamingAnswerQuestionAsync returned (invocation completed) (POST)");
        } catch (Exception ex) {
            log.error("[SSE] exception when starting streamingAnswerQuestionAsync (POST)", ex);
            sendSafe(emitter, SseEmitter.event().name("error").data(String.valueOf(ex)));
            try {
                emitter.complete();
            } catch (Exception ignore) {
            }
        }
        return emitter;
    }

    @AutoResult
    @GetMapping("/aiAssistants")
    public List<AIAssistantVO> getAIAssistants() {
        return aiAssistantApplicationService.getAIAssistants();
    }

    @AutoResult
    @GetMapping("/{aiAssistantId}/conversation-log")
    public List<Pair<String, String>> getConversationLog(@PathVariable String aiAssistantId) {
        return aiAssistantApplicationService.getConversationLog(aiAssistantId);
    }

    /**
     *
     * @return SseEmitter 实例，已配置较长超时和定期心跳发送以保持连接活跃，并且在发送失败时安全地完成连接而不触发全局异常处理器返回 JSON（避免 HttpMessageNotWritableException）
     */
    private SseEmitter createEmitter() {
        // 将超时设置为更长的时间，避免默认超时导致中断（这里设置为 30 分钟，可按需调整）
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(30));

        // 标识连接是否仍然可写，心跳任务在发送前检查
        AtomicBoolean open = new AtomicBoolean(true);

        // 每个 emitter 注册一个定期心跳任务，间隔 15 秒（可根据需要调整）
        ScheduledFuture<?> future = keepAliveScheduler.scheduleAtFixedRate(() -> {
            try {
                if (!open.get()) return;
                // 发送轻量心跳事件，防止中间代理或浏览器在长时间无数据时断开连接
                sendSafe(emitter, SseEmitter.event().name("heartbeat").data("ping"), open);
            } catch (Exception e) {
                // 发送失败时记录日志，实际取消由 onCompletion/onError/onTimeout 处理
                log.debug("Failed to send heartbeat", e);
            }
        }, 15, 15, TimeUnit.SECONDS);

        emitter.onCompletion(() -> {
            try {
                future.cancel(true);
            } catch (Exception ignore) {
            }
            open.set(false);
            log.debug("SSE emitter completed");
        });

        emitter.onTimeout(() -> {
            try {
                // 尝试发送超时通知，但不要触发完整的异常处理流程
                sendSafe(emitter, SseEmitter.event().name("error").data("timeout"), open);
            } catch (Exception ignore) {
            }
            try {
                future.cancel(true);
            } catch (Exception ignore) {
            }
            open.set(false);
            emitter.complete();
            log.warn("SSE emitter timed out");
        });

        emitter.onError(e -> {
            log.error("SSE error", e);
            try {
                future.cancel(true);
            } catch (Exception ignore) {
            }
            open.set(false);
        });
        return emitter;
    }

    // 统一 sendSafe：可选接收 open 标志，避免重复实现
    private void sendSafe(SseEmitter emitter, SseEmitter.SseEventBuilder event, AtomicBoolean... openRefs) {
        try {
            emitter.send(event);
        } catch (IOException e) {
            log.error("Failed to send SSE", e);
            // 不要调用 completeWithError，因为这会进入全局异常处理器并尝试以 text/event-stream 返回 JSON，导致 HttpMessageNotWritableException
            try {
                emitter.complete();
            } catch (Exception ignore) {
            }
            if (openRefs != null && openRefs.length > 0 && openRefs[0] != null) {
                openRefs[0].set(false);
            }
        }
    }
}
