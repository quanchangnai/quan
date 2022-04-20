package quan.generator.rpc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author quanchangnai
 */
public abstract class ServiceElement {

    protected String name;

    protected String comment;

    //泛型的类型参数，参数名：类型上边界<T extends Object&Runnable>
    protected LinkedHashMap<String, List<String>> originalTypeParameters = new LinkedHashMap<>();

    //原始数据的类型都是全类名，这里的类型名都是优化导入之后的类型名，大部分会变成简单类名
    protected LinkedHashMap<String, List<String>> optimizedTypeParameters = new LinkedHashMap<>();

    protected ServiceClass serviceClass;

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String[] getComments() {
        if (comment == null) {
            return new String[0];
        } else {
            return comment.split("\n");
        }
    }

    public LinkedHashMap<String, List<String>> getOriginalTypeParameters() {
        return originalTypeParameters;
    }

    public void setOriginalTypeParameters(LinkedHashMap<String, List<String>> originalTypeParameters) {
        this.originalTypeParameters = originalTypeParameters;
    }

    public String getTypeParametersStr() {
        StringBuilder sb = new StringBuilder();

        if (!optimizedTypeParameters.isEmpty()) {
            sb.append("<");
            int i = 0;
            for (String typeName : optimizedTypeParameters.keySet()) {
                if (i++ > 0) {
                    sb.append(",");
                }
                sb.append(typeName);

                List<String> typeBounds = new ArrayList<>(optimizedTypeParameters.get(typeName));
                if (originalTypeParameters.get(typeName).contains(Object.class.getName())) {
                    typeBounds.remove(0);
                }
                if (!typeBounds.isEmpty()) {
                    sb.append(" extends ");
                    sb.append(String.join(" & ", typeBounds));
                }
            }
            sb.append("> ");
        }

        return sb.toString();
    }

    public ServiceClass getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(ServiceClass serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getGeneratorName() {
        return RpcGenerator.class.getName();
    }

    public void optimizeImport4Proxy() {
        optimizedTypeParameters.clear();
        for (String name : originalTypeParameters.keySet()) {
            for (String bound : originalTypeParameters.get(name)) {
                optimizedTypeParameters.computeIfAbsent(name, k -> new ArrayList<>()).add(serviceClass.optimizeImport(bound));
            }
        }
    }

}
