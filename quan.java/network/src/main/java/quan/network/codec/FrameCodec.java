package quan.network.codec;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 帧长度可变的编解码器，基于一个长度字段实现
 *
 * @author quanchangnai
 */
public class FrameCodec extends Codec {

    /**
     * 长度字段本身占多长
     */
    private final int lengthFieldLength;

    /**
     * 总长度是否包含长度字段
     */
    private final boolean includeLengthField;

    /**
     * 上次解码缓存下来的半包
     */
    private ByteBuffer lastBuffer;

    {
        encodeTypes.add(ByteBuffer.class);
        encodeTypes.add(byte[].class);
        decodeTypes.add(ByteBuffer.class);
    }

    public FrameCodec() {
        this.lengthFieldLength = 2;
        this.includeLengthField = false;
    }

    public FrameCodec(int lengthFieldLength, boolean includeLengthField) {
        super();
        this.lengthFieldLength = lengthFieldLength;
        this.includeLengthField = includeLengthField;
        validateLengthFieldLength();
    }


    @Override
    protected List<Object> decode(Object msg) {
        ByteBuffer srcMsgBuffer;
        if (msg instanceof ByteBuffer) {
            srcMsgBuffer = (ByteBuffer) msg;
        } else {
            srcMsgBuffer = ByteBuffer.wrap((byte[]) msg);
        }

        List<Object> decodedMsgBuffers = new LinkedList<>();
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
                int dataLength = includeLengthField ? length - lengthFieldLength : length;
                if (srcMsgBuffer.remaining() >= dataLength) {
                    byte[] decodedBytes = new byte[dataLength];
                    srcMsgBuffer.get(decodedBytes);
                    decodedMsgBuffers.add(ByteBuffer.wrap(decodedBytes));
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

    @Override
    protected Object encode(Object msg) {
        ByteBuffer srcMsgBuffer = (ByteBuffer) msg;
        int length = srcMsgBuffer.remaining();
        ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(lengthFieldLength + length);
        if (includeLengthField) {
            length += lengthFieldLength;
        }
        putLengthField(encodedMsgBuffer, length);

        encodedMsgBuffer.put(srcMsgBuffer);
        encodedMsgBuffer.flip();
        return encodedMsgBuffer;
    }

    private void validateLengthFieldLength() {
        List<Integer> allowLengthFieldLengthList = Arrays.asList(1, 2, 4);
        if (!allowLengthFieldLengthList.contains(lengthFieldLength)) {
            throw new IllegalArgumentException("lengthFieldLength不合法，允许的值为：" + allowLengthFieldLengthList);
        }
    }

    private void putLengthField(ByteBuffer byteBuffer, int length) {
        if (length < 1) {
            throw new RuntimeException("帧长度错误:" + length);
        }
        switch (lengthFieldLength) {
            case 1:
                if (length > Byte.MAX_VALUE) {
                    throw new RuntimeException("允许的最大帧长度" + Byte.MAX_VALUE + "，实际帧长度为:" + length);
                }
                byteBuffer.put((byte) length);
                break;
            case 2:
                if (length > Short.MAX_VALUE) {
                    throw new RuntimeException("允许的最大帧长度" + Short.MAX_VALUE + "，实际帧长度为:" + length);
                }
                byteBuffer.putShort((short) length);
                break;
            case 4:
                byteBuffer.putInt(length);
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
            default:
                break;
        }

        return length;
    }

}
