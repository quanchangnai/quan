package quan.rpc.test;

import quan.rpc.RPC;
import quan.rpc.Service;

public class RoleService extends Service {

    @Override
    public Object getId() {
        return "role";
    }

    @RPC
    public void login() {

    }

}
