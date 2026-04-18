package host.hunger.vocalchat.shared.trace;

public final class RequestTraceContext {
    private static final ThreadLocal<TraceLog> REQUEST_HOLDER = new ThreadLocal<>();

    private RequestTraceContext() {
    }

    public static void set(TraceLog traceLog) {
        REQUEST_HOLDER.set(traceLog);
    }

    public static TraceLog get() {
        return REQUEST_HOLDER.get();
    }

    public static void clear() {
        REQUEST_HOLDER.remove();
    }
}
