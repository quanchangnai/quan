package quan.rpc.protocol;

import quan.rpc.serialize.ObjectReader;
import quan.rpc.serialize.ObjectWriter;

/**
 * @author quanchangnai
 */
public class PingPong extends Protocol {

    private long time;

    protected PingPong() {
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
