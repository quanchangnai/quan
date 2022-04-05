package quan.generator.rpc;

import java.util.LinkedHashMap;

public class RpcMethod {

    private String name;

    public String returnType;

    //参数名:参数类型
    private LinkedHashMap<String, String> parameters = new LinkedHashMap<>();

    public RpcMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean isReturnVoid() {
        return Void.class.getName().equals(returnType);
    }

    public void addParameter(String type, String name) {
        parameters.put(name, type);
    }

    public LinkedHashMap<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "RpcMethod{" +
                "name='" + name + '\'' +
                ", returnType='" + returnType + '\'' +
                ", parameters=" + parameters +
                '}';
    }

}
