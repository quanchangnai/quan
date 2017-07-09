package quan.network.handler.codec;

import quan.network.handler.HandlerContext;
import quan.network.handler.InboundHandler;
import quan.network.handler.OutboundHandler;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 字符串编码解码器
 *
 * @author quanchangnai
 */
public class StringCodec implements InboundHandler, OutboundHandler {

    private final String charsetName;

    private CharsetDecoder charsetDecoder;

    public StringCodec() {
        charsetName = "UTF-8";
        Charset charset = Charset.forName(charsetName);
        charsetDecoder = charset.newDecoder();
    }

    public StringCodec(String charsetName) {
        this.charsetName = charsetName;
        Charset charset = Charset.forName(charsetName);
        charsetDecoder = charset.newDecoder();
    }

    @Override
    public void onReceived(HandlerContext handlerContext, Object msg) throws Exception {
        if (msg instanceof ByteBuffer) {
            ByteBuffer msgBuffer = (ByteBuffer) msg;
            CharBuffer charBuffer = charsetDecoder.decode(msgBuffer);
            String decodedMsg = charBuffer.toString();
            handlerContext.triggerReceived(decodedMsg);
        } else {
            handlerContext.triggerReceived(msg);
        }
    }

    @Override
    public void onSend(HandlerContext handlerContext, Object msg) throws Exception {
        if (msg instanceof String) {
            String msgStr = (String) msg;
            handlerContext.send(ByteBuffer.wrap(msgStr.getBytes(charsetName)));
        } else {
            handlerContext.send(msg);
        }
    }
}
