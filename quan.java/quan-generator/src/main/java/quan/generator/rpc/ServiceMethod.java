package quan.generator.rpc;

import java.util.LinkedHashMap;
import java.util.List;

public class ServiceMethod extends ServiceElement {

    private int securityModifier;

    public String originalReturnType;

    private boolean varArgs;

    //参数名:参数类型
    private LinkedHashMap<String, String> originalParameters = new LinkedHashMap<>();

    public String optimizedReturnType;

    //参数名:优化导入后的参数类型
    private LinkedHashMap<String, String> optimizedParameters = new LinkedHashMap<>();

    public ServiceMethod(CharSequence name) {
        this.name = name.toString();
    }

    public void setParamSafe(boolean paramSafe) {
        if (paramSafe) {
            securityModifier |= 0b01;
        }
    }

    public void setResultSafe(boolean resultSafe) {
        if (resultSafe) {
            securityModifier |= 0b10;
        }
    }

    public int getSecurityModifier() {
        return securityModifier;
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

    public boolean isVarArgs() {
        return varArgs;
    }

    public void setVarArgs(boolean varArgs) {
        this.varArgs = varArgs;
    }

    public void addParameter(CharSequence name, String type) {
        originalParameters.put(name.toString(), type);
    }

    public boolean isNeedCastArray(String parameterName) {
        String parameterType = originalParameters.get(parameterName);
        if (!parameterType.endsWith("[]")) {
            return false;
        }
        String componentType = parameterType.substring(0, parameterType.length() - 2);
        if (componentType.equals(String.class.getName())) {
            return false;
        }
        try {
            return !Class.forName(componentType).isPrimitive();
        } catch (ClassNotFoundException ignored) {
            return true;
        }
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
        if (serviceClass.getMethodNameNums().get(name) == 1) {
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
            typeBounds = serviceClass.originalTypeParameters.get(type);
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
        optimizedReturnType = serviceClass.optimizeImport(originalReturnType);

        optimizedParameters.clear();
        int i = 0;

        for (String name : originalParameters.keySet()) {
            String parameterType = serviceClass.optimizeImport(originalParameters.get(name));
            if (varArgs && ++i == originalParameters.size()) {
                parameterType = parameterType.replace("[]", "...");
            }
            optimizedParameters.put(name, parameterType);
        }
    }

    public void optimizeImport4Caller() {
        optimizedParameters.clear();
        for (String name : originalParameters.keySet()) {
            String parameterType = originalParameters.get(name);
            parameterType = eraseParameterType(parameterType);
            optimizedParameters.put(name, serviceClass.optimizeImport(parameterType));
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
