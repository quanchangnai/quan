package quan.rpc.test;

import quan.rpc.Transferable;
import quan.rpc.msg.Handshake;
import quan.rpc.msg.Request;
import quan.rpc.msg.Response;

import java.util.function.Function;

/**
 * @author quanchangnai
 */
public class TransferableFactory implements Function<Integer, Transferable> {

    @Override
    public Transferable apply(Integer id) {
        if (id == Handshake.class.getName().hashCode()) {
            return new Handshake();
        } else if (id == Request.class.getName().hashCode()) {
            return new Request();
        } else if (id == Response.class.getName().hashCode()) {
            return new Response();
        }

        return null;
    }

}
