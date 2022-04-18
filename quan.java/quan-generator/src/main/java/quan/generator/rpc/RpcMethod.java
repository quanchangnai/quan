package quan.generator.rpc;

import java.util.LinkedHashMap;
import java.util.List;

public class RpcMethod extends RpcElement {

    public String originalReturnType;

    //参数名:参数类型
    private LinkedHashMap<String, String> originalParameters = new LinkedHashMap<>();

    public String optimizedReturnType;

    //参数名:优化导入后的参数类型
    private LinkedHashMap<String, String> optimizedParameters = new LinkedHashMap<>();

    public RpcMethod(CharSequence name) {
        this.name = name.toString();
    }

    public String getOriginalReturnType() {
        return originalReturnType;
    }

    public void setOriginalReturnType(String originalReturnType) {
        this.originalReturnType = originalReturnType;
    }

    public String getOptimizedReturnType() {
        return optimizedReturnType;
    }

    public boolean isReturnVoid() {
        return Void.class.getSimpleName().equals(optimizedReturnType);
    }

    public void addParameter(CharSequence name, String type) {
        originalParameters.put(name.toString(), type);
    }

    public LinkedHashMap<String, String> getOriginalParameters() {
        return originalParameters;
    }

    public LinkedHashMap<String, String> getOptimizedParameters() {
        return optimizedParameters;
    }

    public String getSignature() {
        StringBuilder signature = new StringBuilder();
        signature.append(name);
        if (rpcClass.getMethodNameNums().get(name) == 1) {
            return signature.toString();
        }

        signature.append("(");
        int i = 0;
        for (String parameterType : optimizedParameters.values()) {
            if (i++ > 0) {
                signature.append(", ");
            }
            signature.append(eraseParameterType(parameterType));
        }
        signature.append(")");
        return signature.toString();
    }

    //擦除方法参数的泛型
    public String eraseParameterType(String type) {
        int index = type.indexOf("<");
        if (index > 0) {
            return type.substring(0, index);
        }

        List<String> typeBounds = originalTypeParameters.get(type);
        if (typeBounds == null || typeBounds.isEmpty()) {
            typeBounds = rpcClass.originalTypeParameters.get(type);
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

    public void optimizeImport4Proxy() {
        super.optimizeImport4Proxy();
        optimizedReturnType = rpcClass.optimizeImport(originalReturnType);

        optimizedParameters.clear();
        for (String name : originalParameters.keySet()) {
            String parameterType = originalParameters.get(name);//全类名还可能会带泛型
            optimizedParameters.put(name, rpcClass.optimizeImport(parameterType));
        }
    }

    public void optimizeImport4Caller() {
        optimizedParameters.clear();
        for (String name : originalParameters.keySet()) {
            String parameterType = originalParameters.get(name);
            parameterType = eraseParameterType(parameterType);
            optimizedParameters.put(name, rpcClass.optimizeImport(parameterType));
        }
    }

    @Override
    public String toString() {
        return "RpcMethod{" +
                "name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", typeParameters=" + originalTypeParameters +
                ", returnType='" + originalReturnType + '\'' +
                ", parameters=" + originalParameters +
                '}';
    }

}
