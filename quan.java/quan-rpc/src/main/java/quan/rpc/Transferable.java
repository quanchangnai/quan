package quan.rpc;

/**
 * 实现了此接口的对象可以通过{@link ObjectWriter}和{@link ObjectReader}进行序列化和反序列化
 *
 * @author quanchangnai
 */
public interface Transferable {

    /**
     * 序列化
     */
    void transferTo(ObjectWriter writer);

    /**
     * 反序列化
     */
    void transferFrom(ObjectReader reader);

}
