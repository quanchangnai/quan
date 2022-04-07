package quan.generator.rpc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author quanchangnai
 */
public abstract class RpcElement {

    protected String name;

    protected String comment = "";

    //泛型的类型参数，参数名：类型边界
    protected LinkedHashMap<String, List<String>> typeParameters = new LinkedHashMap<>();

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (comment != null) {
            this.comment = comment;
        }
    }

    public LinkedHashMap<String, List<String>> getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(LinkedHashMap<String, List<String>> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public String getTypeParametersStr() {
        StringBuilder sb = new StringBuilder();

        if (!typeParameters.isEmpty()) {
            sb.append("<");
            int i = 0;
            for (String typeName : typeParameters.keySet()) {
                if (i++ > 0) {
                    sb.append(",");
                }
                sb.append(typeName);

                List<String> typeBounds = new ArrayList<>(typeParameters.get(typeName));
                typeBounds.remove(Object.class.getName());
                if (!typeBounds.isEmpty()) {
                    sb.append(" extends ");
                    sb.append(String.join("&", typeBounds));
                }
            }
            sb.append(">");
        }

        sb.append(" ");

        return sb.toString();
    }

}
