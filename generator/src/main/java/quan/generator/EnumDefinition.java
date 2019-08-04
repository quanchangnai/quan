package quan.generator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class EnumDefinition extends ClassDefinition {

    private Set<Integer> enumValues = new HashSet<>();

    @Override
    public int getDefinitionType() {
        return 1;
    }

    @Override
    protected void validateField(FieldDefinition fieldDefinition) {
        super.validateField(fieldDefinition);

        int enumValue = 0;
        try {
            enumValue = Integer.parseInt(fieldDefinition.getValue());
        } catch (NumberFormatException ignored) {
        }

        if (enumValue < 1) {
            addValidatedError(getName4Validate() + "的值[" + fieldDefinition.getValue() + "]必须为正整数");
        } else if (enumValues.contains(enumValue)) {
            addValidatedError(getName4Validate() + "的值[" + fieldDefinition.getValue() + "]不能重复");
        } else {
            enumValues.add(enumValue);
            fieldDefinition.setValue(fieldDefinition.getValue());
        }
    }

    public static boolean isEnumDefinition(String type) {
        return ClassDefinition.getClass(type) instanceof EnumDefinition;
    }

    @Override
    public String getDefinitionTypeName() {
        return "枚举";
    }
}
