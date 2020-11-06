package quan.definition.parser;

import org.apache.commons.lang3.StringUtils;
import quan.definition.FieldDefinition;
import quan.definition.config.ConfigDefinition;

import java.io.File;

/**
 * 基于表格的定义文件解析器，在表格中直接定义配置，不支持定义复杂结构
 */
public abstract class TableDefinitionParser extends DefinitionParser {

    @Override
    protected void parseClasses(File definitionFile) {
        String tableName = definitionFile.getName().substring(0, definitionFile.getName().lastIndexOf("."));
        ConfigDefinition configDefinition = new ConfigDefinition(tableName, null);
        configDefinition.setParser(this);
        configDefinition.setDefinitionFile(definitionFile.getName());
        if (tableName.contains(".")) {
            configDefinition.setPackageName(tableName.substring(0, tableName.lastIndexOf(".")));
            configDefinition.setName(tableName.substring(tableName.lastIndexOf(".") + 1));
        } else {
            configDefinition.setName(tableName);
        }

        if (parseTable(configDefinition, definitionFile)) {
            parsedClasses.add(configDefinition);
        }
    }

    protected abstract boolean parseTable(ConfigDefinition configDefinition, File definitionFile);

    protected void addField(ConfigDefinition configDefinition, String columnName, String fieldName, String constraint) {
        if (StringUtils.isBlank(constraint)) {
            addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]约束不能为空");
        }
        String[] constraints = constraint.split(";", -1);

        String fieldType = null;
        String fieldRef = null;
        String fieldIndex = null;
        String fieldOptional = null;

        if (constraints.length > 0) {
            fieldType = constraints[0];
        }

        for (int i = 1; i < constraints.length; i++) {
            String[] constraintNameAndValue = constraints[i].split("=", -1);
            if (constraintNameAndValue.length != 2) {
                addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]约束[" + constraint + "]格式错误");
                break;
            }

            String constraintName = constraintNameAndValue[0];
            switch (constraintName) {
                case "ref":
                    fieldRef = constraintNameAndValue[1];
                    break;
                case "index":
                    fieldIndex = constraintNameAndValue[1];
                    break;
                case "optional":
                    fieldOptional = constraintNameAndValue[1];
                    break;
                default:
                    addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]不支持该约束类型:" + constraintName);
                    break;
            }
        }

        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setParser(this);
        fieldDefinition.setCategory(getCategory());
        fieldDefinition.setName(fieldName);
        fieldDefinition.setTypes(fieldType);
        fieldDefinition.setColumn(columnName);
        fieldDefinition.setIndex(fieldIndex);
        fieldDefinition.setRef(fieldRef);
        fieldDefinition.setOptional(fieldOptional);

        configDefinition.addField(fieldDefinition);
    }

}
