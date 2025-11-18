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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/llm")
public class AIAssistantController {

    private final AIAssistantApplicationService aiAssistantApplicationService;

    // 复用的调度器，用于发送心跳，避免为每个连接创建线程池
    private static final ScheduledExecutorService KEEP_ALIVE_SCHEDULER = Executors.newScheduledThreadPool(1, r -> {
        Thread t = new Thread(r, "sse-keepalive");
        t.setDaemon(true);
        return t;
    });


    @PostMapping("/createNewAssistant")
    public void createNewAssistant(@RequestBody AIAssistantConfigDTO aiAssistantConfigDTO) {
        aiAssistantApplicationService.createNewAssistant(aiAssistantConfigDTO.getName(), aiAssistantConfigDTO.getDescription(), aiAssistantConfigDTO.getCharacter());
    }


    @PostMapping("/generateReply")
    @SkipToken
    public String generateReply(@RequestBody QuestionDTO questionDTO) {
        return aiAssistantApplicationService.answerQuestion(questionDTO.getQuestion(), questionDTO.getAiAssistantId());
    }

    // 明确声明 consumes 为 JSON，方便诊断 Content-Type 导致的匹配问题
    @PostMapping(value = "/streamGenerateReply", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SkipToken
    public SseEmitter streamGenerateReply(@RequestBody QuestionDTO questionDTO) {
        log.info("[SSE] POST /streamGenerateReply received: aiAssistantId={}, questionLength={}", questionDTO.getAiAssistantId(), questionDTO.getQuestion() == null ? 0 : questionDTO.getQuestion().length());
        SseEmitter emitter = createEmitter();

        try {
            log.debug("[SSE] invoking application service streamingAnswerQuestionAsync (POST)");

            // send an immediate 'started' event so caller can see the service was triggered
            sendSafe(emitter, SseEmitter.event().name("started").data("invoked"));

            aiAssistantApplicationService.streamingAnswerQuestionAsync(
                    questionDTO.getQuestion(),
                    questionDTO.getAiAssistantId(),
                    token -> {
                        try {
                            log.debug("[SSE] token callback invoked, tokenLength={}", token == null ? 0 : token.length());
                            sendSafe(emitter, SseEmitter.event().name("token").data(token == null ? "" : token));
                        } catch (Exception e) {
                            log.error("[SSE] exception inside token callback", e);
                        }
                    },
                    () -> {
                        try {
                            log.info("[SSE] completed callback invoked");
                            sendSafe(emitter, SseEmitter.event().name("done").data("complete"));
                        } catch (Exception e) {
                            log.error("[SSE] exception inside completed callback", e);
                        } finally {
                            emitter.complete();
                        }
                    },
                    ex -> {
                        try {
                            log.error("[SSE] error callback invoked", ex);
                            sendSafe(emitter, SseEmitter.event().name("error").data(String.valueOf(ex)));
                        } catch (Exception e) {
                            log.error("[SSE] exception inside error callback", e);
                        } finally {
                            // 安全完成，不触发全局异常处理器在 SSE 流上返回 JSON
                            try { emitter.complete(); } catch (Exception ignore) {}
                        }
                    }
            );

            log.info("[SSE] streamingAnswerQuestionAsync returned (invocation completed)");

        } catch (Exception ex) {
            log.error("[SSE] exception when starting streamingAnswerQuestionAsync", ex);
            sendSafe(emitter, SseEmitter.event().name("error").data(String.valueOf(ex)));
            try { emitter.complete(); } catch (Exception ignore) {}
        }
        return emitter;
    }

    @GetMapping(value = "/streamGenerateReply", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @SkipToken
    public SseEmitter streamGenerateReplyGet(@RequestParam("question") String question,
                                             @RequestParam("aiAssistantId") String aiAssistantId) {
        log.info("[SSE] GET /streamGenerateReply received: aiAssistantId={}, questionLength={}", aiAssistantId, question == null ? 0 : question.length());
        SseEmitter emitter = createEmitter();
        try {
            log.debug("[SSE] invoking application service streamingAnswerQuestionAsync (GET)");
            sendSafe(emitter, SseEmitter.event().name("started").data("invoked"));
            aiAssistantApplicationService.streamingAnswerQuestionAsync(
                    question,
                    aiAssistantId,
                    token -> {
                        try {
                            log.debug("[SSE] token callback invoked (GET), tokenLength={}", token == null ? 0 : token.length());
                            sendSafe(emitter, SseEmitter.event().name("token").data(token == null ? "" : token));
                        } catch (Exception e) {
                            log.error("[SSE] exception inside token callback (GET)", e);
                        }
                    },
                    () -> {
                        try {
                            log.info("[SSE] completed callback invoked (GET)");
                            sendSafe(emitter, SseEmitter.event().name("done").data("complete"));
                        } catch (Exception e) {
                            log.error("[SSE] exception inside completed callback (GET)", e);
                        } finally {
                            emitter.complete();
                        }
                    },
                    ex -> {
                        try {
                            log.error("[SSE] error callback invoked (GET)", ex);
                            sendSafe(emitter, SseEmitter.event().name("error").data(String.valueOf(ex)));
                        } catch (Exception e) {
                            log.error("[SSE] exception inside error callback (GET)", e);
                        } finally {
                            try { emitter.complete(); } catch (Exception ignore) {}
                        }
                    }
            );
            log.info("[SSE] streamingAnswerQuestionAsync returned (invocation completed) (GET)");
        } catch (Exception ex) {
            log.error("[SSE] exception when starting streamingAnswerQuestionAsync (GET)", ex);
            sendSafe(emitter, SseEmitter.event().name("error").data(String.valueOf(ex)));
            try { emitter.complete(); } catch (Exception ignore) {}
        }
        return emitter;
    }

    private SseEmitter createEmitter() {
        // 将超时设置为更长的时间，避免默认超时导致中断（这里设置为 30 分钟，可按需调整）
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(30));

        // 标识连接是否仍然可写，心跳任务在发送前检查
        AtomicBoolean open = new AtomicBoolean(true);

        // 每个 emitter 注册一个定期心跳任务，间隔 15 秒（可根据需要调整）
        ScheduledFuture<?> future = KEEP_ALIVE_SCHEDULER.scheduleAtFixedRate(() -> {
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

    // 修改后的 sendSafe：加入 open 标志引用，IOException 不再触发 completeWithError（避免全局异常处理在 text/event-stream 上返回 JSON）
    private void sendSafe(SseEmitter emitter, SseEmitter.SseEventBuilder event, AtomicBoolean open) {
        try {
            emitter.send(event);
        } catch (IOException e) {
            log.error("Failed to send SSE", e);
            // 不要调用 completeWithError，因为这会进入全局异常处理器并尝试以 text/event-stream 返回 JSON，导致 HttpMessageNotWritableException
            try {
                emitter.complete();
            } catch (Exception ignore) {
            }
            open.set(false);
        }
    }

    // 兼容旧的 sendSafe 调用签名（保留原实现以避免大量改动），内部调用新实现并传递一个默认的 open 标志
    private void sendSafe(SseEmitter emitter, SseEmitter.SseEventBuilder event) {
        // 在这里我们无法访问 createEmitter 的 open 标志，因此进行 best-effort：尝试发送且在失败时安全完成
        try {
            emitter.send(event);
        } catch (IOException e) {
            log.error("Failed to send SSE (no-open-flag)", e);
            try {
                emitter.complete();
            } catch (Exception ignore) {
            }
        }
    }
}
