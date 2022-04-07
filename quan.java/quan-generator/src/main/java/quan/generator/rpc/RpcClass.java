package quan.generator.rpc;

import java.util.ArrayList;
import java.util.List;

public class RpcClass extends RpcElement {

    private String fullName;

    private String packageName;

    private List<RpcMethod> methods = new ArrayList<>();

    public RpcClass(String fullName) {
        this.fullName = fullName;
        int index = fullName.lastIndexOf(".");
        if (index > 0) {
            this.packageName = fullName.substring(0, index);
            this.name = fullName.substring(index + 1);
        } else {
            this.name = fullName;
        }
    }

    public String getFullName() {
        return fullName;
    }

    public String getPackageName() {
        return packageName;
    }

    public List<RpcMethod> getMethods() {
        return methods;
    }

    @Override
    public String toString() {
        return "RpcClass{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", typeParameters=" + typeParameters +
                ", comment='" + comment + '\'' +
                ", methods=" + methods +
                '}';
    }

}
