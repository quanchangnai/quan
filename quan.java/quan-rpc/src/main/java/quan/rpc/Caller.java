package quan.rpc;

public  interface Caller {

    Object call(Service service, int methodId, Object... params);

}
