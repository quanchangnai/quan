package quan.generator.message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class ClassDefinition extends Definition {

    private String packageName;

    private List<FieldDefinition> fields = new ArrayList<>();

    @Override
    public int getDefinitionType() {
        return TYPE_ENUM;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public String getFullName() {
        return packageName + "." + getName();
    }

    @Override
    public String toString() {
        return getClass().getName()+"{" +
                "name=" + getName() +
                ",packageName=" + getPackageName() +
                ",fields=" + getFields() +
                '}';
    }
}
