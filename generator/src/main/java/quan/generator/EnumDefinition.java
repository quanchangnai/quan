package quan.generator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class EnumDefinition extends ClassDefinition {

    @Override
    public int getDefinitionType() {
        return 1;
    }

    @Override
    public void validate() {
        super.validate();

    }

    private Set<Integer> enumValues = new HashSet<>();

    @Override
    protected void validateField(FieldDefinition fieldDefinition) {
        super.validateField(fieldDefinition);

        int enumValue = 0;
        try {
            enumValue = Integer.parseInt(fieldDefinition.getValue());
        } catch (NumberFormatException ignored) {
        }

        if (enumValue < 1) {
            addValidatedError("枚举" + getName4Validate() + "的值必须为正整数");
        } else if (enumValues.contains(enumValue)) {
            addValidatedError("枚举" + getName4Validate() + "的值不能重复");
        } else {
            enumValues.add(enumValue);
            fieldDefinition.setValue(fieldDefinition.getValue());
        }
    }

    public static boolean isEnumDefinition(String type) {
        return ClassDefinition.getAll().get(type) instanceof EnumDefinition;
    }
}
