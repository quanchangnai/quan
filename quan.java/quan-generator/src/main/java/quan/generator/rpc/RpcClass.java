package quan.generator.rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpcClass extends RpcElement {

    private String fullName;

    private String packageName;

    private List<RpcMethod> methods = new ArrayList<>();

    //简单类名：全类名(可以省略导入的类以-开头)
    private Map<String, String> imports = new HashMap<>();

    public RpcClass(String fullName) {
        this.fullName = fullName;
        int index = fullName.lastIndexOf(".");
        if (index > 0) {
            this.packageName = fullName.substring(0, index);
            this.name = fullName.substring(index + 1);
        } else {
            this.name = fullName;
        }
        this.rpcClass = this;
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
    public void setRpcClass(RpcClass rpcClass) {
        throw new UnsupportedOperationException();
    }

    public Map<String, String> getImports() {
        Map<String, String> imports = new HashMap<>();
        for (String importKey : this.imports.keySet()) {
            String importValue = this.imports.get(importKey);
            if (!importValue.startsWith("-")) {
                imports.put(importKey, importValue);
            }
        }
        return imports;
    }

    /**
     * 优化导入
     *
     * @param genericFullName 全类名，可能会带泛型
     * @return 实际使用的类名，简单类名冲突的使用全类名，不冲突的使用简单类名
     */
    public String optimizeImport(String genericFullName) {
        String fullName = genericFullName;
        String typeParamFullNames = null;
        int index = genericFullName.indexOf("<");
        if (index > 0) {
            fullName = genericFullName.substring(0, index);
            typeParamFullNames = genericFullName.substring(index + 1, genericFullName.length() - 1);
        }

        String usedName = optimizeUsedName(fullName);
        StringBuilder usedGenericName = new StringBuilder();
        usedGenericName.append(usedName);

        if (typeParamFullNames != null) {
            usedGenericName.append("<");
            int i = 0;
            for (String typeParamFullType : typeParamFullNames.split(",")) {
                if (i++ > 0) {
                    usedGenericName.append(", ");
                }
                index = typeParamFullType.lastIndexOf(" ");
                if (index > 0) {
                    usedGenericName.append(typeParamFullType, 0, index + 1);
                    typeParamFullType = typeParamFullType.substring(index + 1);
                }
                usedGenericName.append(optimizeUsedName(typeParamFullType));
            }
            usedGenericName.append(">");
        }

        return usedGenericName.toString();
    }

    /**
     * 优化导入
     *
     * @param fullName 全类名，不带泛型
     */
    private String optimizeUsedName(String fullName) {
        int index = fullName.lastIndexOf(".");
        if (index < 0) {
            return fullName;
        }

        String enclosingName = fullName.substring(0, index);
        String simpleName = fullName.substring(index + 1);

        String importValue = imports.get(simpleName);
        if (importValue != null) {
            if (importValue.equals("-" + fullName) || importValue.equals(fullName)) {
                return simpleName;
            } else {
                return fullName;
            }
        }

        if (enclosingName.equals("java.lang") || enclosingName.equals(packageName)) {
            imports.put(simpleName, "-" + fullName);
        } else {
            imports.put(simpleName, fullName);
        }

        return simpleName;
    }

    public void optimizeImport4Proxy() {
        imports.clear();
        imports.put("Promise", "quan.rpc.Promise");
        imports.put("Worker", "quan.rpc.Worker");

        super.optimizeImport4Proxy();
        methods.forEach(RpcMethod::optimizeImport4Proxy);
    }

    public void optimizeImport4Caller() {
        imports.clear();
        imports.put("Caller", "quan.rpc.Caller");
        imports.put("Service", "quan.rpc.Service");

        methods.forEach(RpcMethod::optimizeImport4Caller);
    }

    @Override
    public String toString() {
        return "RpcClass{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", typeParameters=" + originalTypeParameters +
                ", comment='" + comment + '\'' +
                ", methods=" + methods +
                '}';
    }

}
