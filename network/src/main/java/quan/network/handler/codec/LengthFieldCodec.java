package quan.network.handler.codec;

import quan.network.handler.HandlerContext;
import quan.network.handler.InboundHandler;
import quan.network.handler.OutboundHandler;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 基于一个帧长度字段的编解码器
 *
 * @author quanchangnai
 */
public class LengthFieldCodec implements InboundHandler, OutboundHandler {
    /**
     * 长度字段本身占多长
     */
    private final int lengthFieldLength;
    /**
     * 总长度是否包含长度字段
     */
    private final boolean lengthFieldInclude;
    /**
     * 上次解码缓存下来的半包
     */
    private ByteBuffer lastBuffer;

    public LengthFieldCodec() {
        this.lengthFieldLength = 4;
        this.lengthFieldInclude = false;
    }

    public LengthFieldCodec(int lengthFieldLength, boolean lengthFieldInclude) {
        super();
        this.lengthFieldLength = lengthFieldLength;
        this.lengthFieldInclude = lengthFieldInclude;
        validateLengthFieldLength();
    }

    @Override
    public void onReceived(HandlerContext handlerContext, Object msg) throws Exception {
        if (msg instanceof ByteBuffer) {
            ByteBuffer msgBuffer = (ByteBuffer) msg;
            List<ByteBuffer> decodedMsgBuffers = decode(msgBuffer);
            for (ByteBuffer decodedMsgBuffer : decodedMsgBuffers) {
                handlerContext.triggerReceived(decodedMsgBuffer);
            }
        } else {
            handlerContext.triggerReceived(msg);
        }

    }

    @Override
    public void onSend(HandlerContext handlerContext, Object msg) throws Exception {
        if (msg instanceof ByteBuffer) {
            ByteBuffer msgBuffer = (ByteBuffer) msg;
            handlerContext.send(encode(msgBuffer));
        } else {
            handlerContext.send(msg);
        }

    }

    protected List<ByteBuffer> decode(ByteBuffer srcMsgBuffer) {
        List<ByteBuffer> decodedMsgBuffers = new LinkedList<>();
        if (lastBuffer != null && lastBuffer.hasRemaining()) {
            // 拼接上次解码缓存下来的半包
            ByteBuffer srcMsgBuffer0 = srcMsgBuffer;
            srcMsgBuffer = ByteBuffer.allocate(lastBuffer.remaining() + srcMsgBuffer.remaining());
            srcMsgBuffer.put(lastBuffer);
            srcMsgBuffer.put(srcMsgBuffer0);
            srcMsgBuffer.flip();
        }

        // 如果有粘包，则拆分，有半包，则缓存
        while (srcMsgBuffer.hasRemaining()) {
            if (srcMsgBuffer.remaining() >= lengthFieldLength) {
                int length = getLengthField(srcMsgBuffer);
                int lengthExcludeLengthField = length;
                if (lengthFieldInclude) {
                    lengthExcludeLengthField = length - lengthFieldLength;
                }
                if (srcMsgBuffer.remaining() >= lengthExcludeLengthField) {
                    byte[] decodedBytes = new byte[lengthExcludeLengthField];
                    srcMsgBuffer.get(decodedBytes);
                    ByteBuffer decodedBuffer = ByteBuffer.wrap(decodedBytes);
                    decodedMsgBuffers.add(decodedBuffer);
                } else {
                    lastBuffer = ByteBuffer.allocate(lengthFieldLength + srcMsgBuffer.remaining());
                    putLengthField(lastBuffer, length);
                    lastBuffer.put(srcMsgBuffer);
                    lastBuffer.flip();
                }
            } else {
                byte[] lastBytes = new byte[srcMsgBuffer.remaining()];
                srcMsgBuffer.get(lastBytes);
                lastBuffer = ByteBuffer.wrap(lastBytes);
            }
        }

        return decodedMsgBuffers;
    }

    protected ByteBuffer encode(ByteBuffer srcMsgBuffer) {
        int length = srcMsgBuffer.remaining();
        ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(lengthFieldLength + length);
        if (lengthFieldInclude) {
            length += lengthFieldLength;
        }
        putLengthField(encodedMsgBuffer, length);

        encodedMsgBuffer.put(srcMsgBuffer);
        encodedMsgBuffer.flip();
        return encodedMsgBuffer;
    }

    private void validateLengthFieldLength() {
        List<Integer> allowLengthFieldLengthList = Arrays.asList(1, 2, 4, 8);
        if (!allowLengthFieldLengthList.contains(lengthFieldLength)) {
            throw new IllegalArgumentException("lengthFieldLength不合法，允许的值为：" + allowLengthFieldLengthList);
        }
    }

    private void putLengthField(ByteBuffer byteBuffer, int length) {
        switch (lengthFieldLength) {
            case 1:
                byteBuffer.put((byte) length);
                break;
            case 2:
                byteBuffer.putShort((short) length);
                break;
            case 4:
                byteBuffer.putInt(length);
                break;
            case 8:
                byteBuffer.putLong(length);
                break;

            default:
                break;
        }
    }

    private int getLengthField(ByteBuffer byteBuffer) {
        int length = 0;
        switch (lengthFieldLength) {
            case 1:
                length = byteBuffer.get();
                break;
            case 2:
                length = byteBuffer.getShort();
                break;
            case 4:
                length = byteBuffer.getInt();
                break;
            case 8:
                length = (int) byteBuffer.getLong();
                break;
            default:
                break;
        }

        return length;
    }
}
