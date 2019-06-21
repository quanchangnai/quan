package quan.generator.message;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class BeanDefinition extends ClassDefinition {

    private Set<String> imports = new HashSet<>();

    @Override
    public int getDefinitionType() {
        return TYPE_BEAN;
    }

    public Set<String> getImports() {
        return imports;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name=" + getName() +
                ",imports=" + imports +
                ",packageName=" + getPackageName() +
                ",fields=" + getFields() +
                '}';
    }
}
