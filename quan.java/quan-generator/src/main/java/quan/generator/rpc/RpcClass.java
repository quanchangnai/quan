package quan.generator.rpc;

import java.util.ArrayList;
import java.util.List;

public class RpcClass {

    private String fullName;

    private String packageName;

    private String simpleName;

    private List<RpcMethod> methods = new ArrayList<>();

    public RpcClass(String fullName) {
        this.fullName = fullName;
        int index = fullName.lastIndexOf(".");
        if (index > 0) {
            this.packageName = fullName.substring(0, index);
            this.simpleName = fullName.substring(index + 1);
        } else {
            this.simpleName = fullName;
        }
    }

    public String getFullName() {
        return fullName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSimpleName() {
        return simpleName;
    }


    public List<RpcMethod> getMethods() {
        return methods;
    }


    @Override
    public String toString() {
        return "RpcClass{" +
                "packageName='" + packageName + '\'' +
                ", name='" + simpleName + '\'' +
                ", methods=" + methods +
                '}';
    }

}
