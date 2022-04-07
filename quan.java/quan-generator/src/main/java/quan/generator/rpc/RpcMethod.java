package quan.generator.rpc;

import java.util.LinkedHashMap;
import java.util.List;

public class RpcMethod extends RpcElement {

    private RpcClass rpcClass;

    public String returnType;

    //参数名:参数类型
    private LinkedHashMap<String, String> parameters = new LinkedHashMap<>();

    public RpcMethod(CharSequence name) {
        this.name = name.toString();
    }

    public RpcClass getRpcClass() {
        return rpcClass;
    }

    public void setRpcClass(RpcClass rpcClass) {
        this.rpcClass = rpcClass;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean isReturnVoid() {
        return Void.class.getSimpleName().equals(returnType);
    }

    public void addParameter(CharSequence name, String type) {
        parameters.put(name.toString(), type);
    }

    public LinkedHashMap<String, String> getParameters() {
        return parameters;
    }

    //擦除方法参数的泛型
    public String eraseParameterType(String type) {
        int index = type.indexOf("<");
        if (index > 0) {
            return type.substring(0, index);
        }

        List<String> typeBounds = typeParameters.get(type);
        if (typeBounds == null || typeBounds.isEmpty()) {
            typeBounds = rpcClass.typeParameters.get(type);
        }

        if (typeBounds != null && !typeBounds.isEmpty()) {
            if (typeBounds.contains(Object.class.getName()) && typeBounds.size() > 1) {
                return typeBounds.get(1);
            } else {
                return typeBounds.get(0);
            }
        }

        return type;
    }


    @Override
    public String toString() {
        return "RpcMethod{" +
                "name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", typeParameters=" + typeParameters +
                ", returnType='" + returnType + '\'' +
                ", parameters=" + parameters +
                '}';
    }

}
