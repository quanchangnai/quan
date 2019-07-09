package quan.generator;

import quan.generator.ClassDefinition;
import quan.generator.FieldDefinition;

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
        } catch (NumberFormatException e) {
        }
        if (enumValue < 1) {
            throwValidatedError("枚举值必须为正整数");
        }
        if (enumValues.contains(enumValue)) {
            throwValidatedError("枚举值不能重复");
        }

        enumValues.add(enumValue);
        fieldDefinition.setValue(fieldDefinition.getValue());
    }
}
