package quan.rpc.protocol;

import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;

import java.util.Arrays;

/**
 * 握手协议
 *
 * @author quanchangnai
 */
public class Handshake extends Protocol {

    /**
     * 不同的连接器实现的握手参数可能不一样
     */
    private Object[] params;

    protected Handshake() {
    }

    public Handshake(int serverId, Object... params) {
        super(serverId);
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParam(int index) {
        return (T) params[index];
    }

    @Override
    public void transferTo(ObjectWriter writer) {
        super.transferTo(writer);
        writer.write(params);
    }

    @Override
    public void transferFrom(ObjectReader reader) {
        super.transferFrom(reader);
        params = reader.read();
    }

    @Override
    public String toString() {
        return "Handshake{" +
                "params=" + Arrays.toString(params) +
                '}';
    }
}
