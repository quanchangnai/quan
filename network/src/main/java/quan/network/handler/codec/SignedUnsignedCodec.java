package quan.network.handler.codec;

import quan.network.handler.Handler;
import quan.network.handler.HandlerContext;

import java.nio.ByteBuffer;

/**
 * 有符号字节流编码成无符号的，无符号字节流解码成有符号的
 * Created by quanchangnai on 2019/7/15.
 */
public class SignedUnsignedCodec implements Handler<Object> {

    @Override
    public void onReceived(HandlerContext handlerContext, Object msg) throws Exception {
        if (msg instanceof ByteBuffer) {
            ByteBuffer msgBuffer = (ByteBuffer) msg;
        } else {
            handlerContext.triggerReceived(msg);
        }
    }

    @Override
    public void onSend(HandlerContext handlerContext, Object msg) throws Exception {
        if (msg instanceof ByteBuffer) {
            ByteBuffer msgBuffer = (ByteBuffer) msg;

        } else if (msg instanceof byte[]) {

        } else {
            handlerContext.send(msg);
        }
    }

    public static void main(String[] args) {
//        byte[] signedBytes = {-12, 30, 49, 120};
//        byte[] unsignedBytes = new byte[signedBytes.length];
//
//        for (int i = 0; i < signedBytes.length; i++) {
//            byte b = signedBytes[i];
//        }

        byte b = -0b1111110;
        System.err.println(b);


        int unsignedInt = Byte.toUnsignedInt(b);
        System.err.println(unsignedInt);
        System.err.println((byte) (unsignedInt & 0b11111111));
        System.err.println(Integer.toBinaryString(unsignedInt));
        System.err.println(Integer.toBinaryString(-126));

    }
}
