package quan.generator.config;

import quan.generator.ClassDefinition;
import quan.generator.FieldDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigDefinition extends ClassDefinition {

    private String source;

    private List<IndexDefinition> indexes = new ArrayList<>();

    public ConfigDefinition() {
    }

    public ConfigDefinition(String source) {
        this.source = source;
    }

    @Override
    public int getDefinitionType() {
        return 6;
    }


    public List<IndexDefinition> getIndexes() {
        return indexes;
    }

    @Override
    public void addField(FieldDefinition fieldDefinition) {
        super.addField(fieldDefinition);

        if (fieldDefinition.getUnique() != null) {
            IndexDefinition indexDefinition = new IndexDefinition(this);
            indexDefinition.getFields().add(fieldDefinition);
            indexDefinition.setUnique(fieldDefinition.getUnique());
            indexes.add(indexDefinition);
        }

    }

    @Override
    public void validate() {
        super.validate();

        if (source == null || source.trim().equals("")) {
            throwValidatedError("配置[" + getName() + "]的来源不能为空");
        }

        for (IndexDefinition indexDefinition : indexes) {
            validateIndex(indexDefinition);
        }

    }

    @Override
    protected void validateField(FieldDefinition fieldDefinition) {
        super.validateField(fieldDefinition);

        if (fieldDefinition.getSource() == null || fieldDefinition.getSource().trim().equals("")) {
            throwValidatedError("字段[" + fieldDefinition.getName() + "]的来源不能为空");
        }

    }

    private void validateIndex(IndexDefinition indexDefinition) {

    }


}
