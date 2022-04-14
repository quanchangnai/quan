package quan.rpc.msg;

import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;
import quan.rpc.serialize.Transferable;

/**
 * @author quanchangnai
 */
public class PingPong implements Transferable {

    private long time;

    public PingPong() {
    }

    public PingPong(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public void transferTo(ObjectWriter writer) {
        writer.write(time);
    }

    @Override
    public void transferFrom(ObjectReader reader) {
        time = reader.read();
    }

}
