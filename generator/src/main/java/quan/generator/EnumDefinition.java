package quan.generator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class EnumDefinition extends ClassDefinition {

    private Map<Integer, FieldDefinition> valuesFields = new HashMap<>();

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

        if (enumValue <= 0) {
            addValidatedError(getName4Validate() + "的值[" + fieldDefinition.getValue() + "]必须为正整数");
        } else if (valuesFields.containsKey(enumValue)) {
            addValidatedError(getName4Validate() + "的值[" + fieldDefinition.getValue() + "]不能重复");
        } else {
            valuesFields.put(enumValue, fieldDefinition);
        }
    }

    public static boolean isEnumDefinition(String type) {
        return ClassDefinition.getClass(type) instanceof EnumDefinition;
    }

    @Override
    public String getDefinitionTypeName() {
        return "枚举";
    }

    public FieldDefinition getField(int enumValue) {
        return valuesFields.get(enumValue);
    }

}
