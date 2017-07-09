package quan.protocol.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class BeanDefinition extends Definition {

    private String packageName;
    private Set<String> imports = new HashSet<>();
    private List<FieldDefinition> fields = new ArrayList<>();

    @Override
    public int getDefinitionType() {
        return DEFINITION_TYPE_BEAN;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<String> getImports() {
        return imports;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }
}
