package quan.rpc;

/**
 * @author quanchangnai
 */
public class CallException extends Exception {

    private long callId;

    private String callee;

    private String message = "";

    public CallException(String message) {
        super(message);
    }

    protected void setCallId(long callId) {
        this.callId = callId;
    }

    protected void setCallee(String callee) {
        this.callee = callee;
    }

    @Override
    public String getMessage() {
        if (callId > 0 && callee != null && message.isEmpty()) {
            message = String.format("调用[%s]方法[%s]返回异常：", callId, callee);
        }
        return message + super.getMessage();
    }

}
