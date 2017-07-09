package quan.protocol;

/**
 * Created by quanchangnai on 2017/7/9.
 */
public abstract class ProtocolHandler<P extends Protocol> {

    private P support;

    public ProtocolHandler(P support) {
        this.support = support;
    }

    public P getSupport() {
        return support;
    }


    public abstract void handle(P p);

}
