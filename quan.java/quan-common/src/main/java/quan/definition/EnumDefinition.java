package quan.definition;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public class EnumDefinition extends ClassDefinition {

    private Map<Integer, FieldDefinition> valuesFields = new HashMap<>();

    @Override
    public int getKind() {
        return KIND_ENUM;
    }

    @Override
    public EnumDefinition setCategory(Category category) {
        this.category = category;
        return this;
    }

    @Override
    public Pattern getNamePattern() {
        Pattern namePattern = parser.getEnumNamePattern();
        if (namePattern == null) {
            namePattern = super.getNamePattern();
        }
        return namePattern;
    }

    @Override
    protected void validateField(FieldDefinition fieldDefinition) {
        super.validateField(fieldDefinition);

        int enumValue = 0;
        try {
            enumValue = Integer.parseInt(fieldDefinition.getEnumValue());
        } catch (NumberFormatException ignored) {
        }

        if (enumValue <= 0) {
            addValidatedError(getValidatedName() + "的值[" + fieldDefinition.getEnumValue() + "]必须为正整数");
        } else if (valuesFields.containsKey(enumValue)) {
            addValidatedError(getValidatedName() + "的值[" + fieldDefinition.getEnumValue() + "]不能重复");
        } else {
            valuesFields.put(enumValue, fieldDefinition);
        }
    }

    @Override
    public String getKindName() {
        return "枚举";
    }

    public FieldDefinition getField(int enumValue) {
        return valuesFields.get(enumValue);
    }

}
