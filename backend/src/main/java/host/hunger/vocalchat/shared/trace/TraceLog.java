package host.hunger.vocalchat.shared.trace;

public class TraceLog {
    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作用户
     */
    private Object user;

    /**
     * 消耗时间
     */
    private String spendTime;

    /**
     * URL
     */
    private String url;

    /**
     * 请求参数
     */
    private Object params;

    /**
     * 请求返回的结果
     */
    private Object result;

    private static final String COMMON_FORMAT = """

            ===========捕获响应===========
            操作描述：%s
            请求地址：%s
            请求参数：%s
            请求返回：%s
            请求用户：%s
            请求耗时：%s
            ===========释放响应===========""";

    private static final String ERROR_FORMAT = """

            ===========捕获异常===========
            操作描述：%s
            请求地址：%s
            请求参数：%s
            请求异常：%s
            请求用户：%s
            请求耗时：%s
            ===========释放异常===========""";

    public TraceLog(String spendTime, String requestURI) {
        this.spendTime = spendTime;
        this.url = requestURI;
    }

    public String getSpendTime() {
        return spendTime;
    }

    public TraceLog setSpendTime(String spendTime) {
        this.spendTime = spendTime;
        return this;
    }

    public TraceLog setDescription(String description) {
        this.description = description;
        return this;
    }

    public TraceLog setUser(Object user) {
        this.user = user;
        return this;
    }

    public TraceLog setParams(Object params) {
        this.params = params;
        return this;
    }

    public TraceLog setResult(Object result) {
        this.result = result;
        return this;
    }

    public String toLogFormat(boolean requestStatus) {
        String strResult = getResult(200);
        String strParam = String.valueOf(params);
        String format = requestStatus ? COMMON_FORMAT : ERROR_FORMAT;
        String strUser = String.valueOf(user);
        return String.format(format, description, url, strParam, strResult, strUser, spendTime);
    }

    /**
     * 截取指定长度的返回值
     */
    public String getResult(int factor) {
        String resultString = String.valueOf(this.result);
        if (factor == 0 || resultString.length() < factor) {
            return resultString;
        }
        return resultString.substring(0, factor - 1) + "...}";
    }
}
