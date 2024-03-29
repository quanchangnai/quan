package quan.network.codec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Collections;
import java.util.List;

/**
 * 字符串编码解码器
 *
 * @author quanchangnai
 */
public class StringCodec extends Codec {

    private final Charset charset;

    private final CharsetDecoder charsetDecoder;

    {
        encodeTypes.add(String.class);
        decodeTypes.add(ByteBuffer.class);
        decodeTypes.add(byte[].class);
    }

    public StringCodec() {
        this("UTF-8");
    }

    public StringCodec(String charsetName) {
        this.charset = Charset.forName(charsetName);
        this.charsetDecoder = charset.newDecoder();
    }

    @Override
    protected List<Object> decode(Object msg) throws Exception {
        ByteBuffer msgBuffer;
        if (msg instanceof ByteBuffer) {
            msgBuffer = (ByteBuffer) msg;
        } else {
            msgBuffer = ByteBuffer.wrap((byte[]) msg);
        }

        return Collections.singletonList(charsetDecoder.decode(msgBuffer).toString());
    }

    @Override
    protected List<Object> encode(Object msg) {
        byte[] bytes = ((String) msg).getBytes(charset);
        return Collections.singletonList(ByteBuffer.wrap(bytes));
    }

}
