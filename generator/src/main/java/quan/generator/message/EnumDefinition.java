package quan.generator.message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class EnumDefinition extends Definition {

    private String packageName;
    private List<FieldDefinition> fields = new ArrayList<>();

    @Override
    public int getDefinitionType() {
        return DEFINITION_TYPE_ENUM;
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

}
