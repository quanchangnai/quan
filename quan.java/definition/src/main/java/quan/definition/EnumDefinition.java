package quan.definition;

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
    public EnumDefinition setCategory(Category category) {
        this.category = category;
        return this;
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
            addValidatedError(getValidatedName() + "的值[" + fieldDefinition.getValue() + "]必须为正整数");
        } else if (valuesFields.containsKey(enumValue)) {
            addValidatedError(getValidatedName() + "的值[" + fieldDefinition.getValue() + "]不能重复");
        } else {
            valuesFields.put(enumValue, fieldDefinition);
        }
    }


    @Override
    public String getDefinitionTypeName() {
        return "枚举";
    }

    public FieldDefinition getField(int enumValue) {
        return valuesFields.get(enumValue);
    }

}
