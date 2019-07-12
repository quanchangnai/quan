package quan.generator.config;

import quan.generator.ClassDefinition;
import quan.generator.FieldDefinition;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigDefinition extends ClassDefinition {

    private String source;

    public ConfigDefinition() {
    }

    public ConfigDefinition(String source) {
        this.source = source;
    }

    @Override
    public int getDefinitionType() {
        return 6;
    }

    @Override
    public void validate() {
        super.validate();

        if (source == null || source.trim().equals("")) {
            throwValidatedError("配置["+getName()+"]的来源不能为空");
        }

    }

    @Override
    protected void validateField(FieldDefinition fieldDefinition) {
        super.validateField(fieldDefinition);

        if (fieldDefinition.getSource() == null || fieldDefinition.getSource().trim().equals("")) {
            throwValidatedError("字段[" + fieldDefinition.getName() + "]的来源不能为空");
        }

    }
}
