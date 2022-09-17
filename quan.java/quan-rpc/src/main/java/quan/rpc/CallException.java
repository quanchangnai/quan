package quan.rpc;

import quan.rpc.protocol.Response;

/**
 * 远程调用异常
 *
 * @author quanchangnai
 */
public class CallException extends Exception {

    private long callId;

    private String signature;

    private String message = "";

    public CallException(String message) {
        super(message);
    }

    protected void setCallId(long callId) {
        this.callId = callId;
    }

    protected void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String getMessage() {
        if (callId > 0 && signature != null && message.isEmpty()) {
            message = String.format("调用[%s]方法[%s]返回异常：", callId, signature);
        }
        return message + super.getMessage();
    }

    public static CallException create(Response response) {
        if (response.getException() != null) {
            return new CallException(response.getException());
        } else {
            return null;
        }
    }

}
