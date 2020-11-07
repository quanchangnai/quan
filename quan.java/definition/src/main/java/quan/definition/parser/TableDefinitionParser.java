package quan.definition.parser;

import org.apache.commons.lang3.StringUtils;
import quan.definition.FieldDefinition;
import quan.definition.config.ConfigDefinition;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

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
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setParser(this);
        fieldDefinition.setCategory(getCategory());
        fieldDefinition.setName(fieldName);
        fieldDefinition.setColumn(columnName);

        configDefinition.addField(fieldDefinition);

        if (StringUtils.isBlank(constraint)) {
            return;
        }

        String[] constraints = constraint.split(";", -1);

        if (constraints.length > 0) {
            fieldDefinition.setTypes(constraints[0]);
        }

        Set<String> constraintNames = new HashSet<>();

        for (int i = 1; i < constraints.length; i++) {
            String[] constraintNameAndValue = constraints[i].split("=", -1);
            if (constraintNameAndValue.length != 2) {
                addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]约束[" + constraint + "]格式错误");
                break;
            }

            String constraintName = constraintNameAndValue[0].trim();
            String constraintValue = constraintNameAndValue[1].trim();

            if (constraintNames.contains(constraintName)) {
                addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]约束类型[" + constraintName + "]重复");
            } else {
                constraintNames.add(constraintName);
            }

            switch (constraintName) {
                case "ref":
                    fieldDefinition.setRef(constraintValue);
                    break;
                case "index":
                    fieldDefinition.setIndex(constraintValue);
                    break;
                case "optional":
                    fieldDefinition.setOptional(constraintValue);
                    break;
                default:
                    addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]不支持该约束类型:" + constraintName);
                    break;
            }
        }
    }

}
