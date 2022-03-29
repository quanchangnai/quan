package quan.rpc;

public abstract class Caller<S extends Service> {

    public abstract Object call(S service, String methodId, Object... methodParams);

}
