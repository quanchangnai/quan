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
    protected void parseFile(File definitionFile) {
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

    protected void addField(ConfigDefinition configDefinition, String columnName, String fieldName, String fieldConstraint) {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setParser(this);
        fieldDefinition.setCategory(getCategory());
        fieldDefinition.setName(fieldName);
        fieldDefinition.setColumn(columnName);
        configDefinition.addField(fieldDefinition);

        if (StringUtils.isBlank(fieldConstraint)) {
            addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]约束不能为空");
            return;
        }

        String[] constraints = fieldConstraint.split(";", -1);

        fieldDefinition.setTypes(constraints[0]);
        Set<String> constraintTypes = new HashSet<>();

        for (int i = 1; i < constraints.length; i++) {
            String constraint = constraints[i].trim();
            String constraintType;
            String constraintValue;

            switch (constraint) {
                case "index":
                    constraintType = "index";
                    constraintValue = "normal";
                    break;
                case "unique":
                    constraintType = "index";
                    constraintValue = "unique";
                    break;
                case "optional":
                    constraintType = "optional";
                    constraintValue = "true";
                    break;
                default:
                    try {
                        String[] constraintTypeAndValue = constraint.split("=", -1);
                        constraintType = constraintTypeAndValue[0].trim();
                        constraintValue = constraintTypeAndValue[1].trim();
                    } catch (Exception ignored) {
                        addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]约束[" + constraint + "]格式错误");
                        continue;
                    }
                    break;
            }

            if (constraintType.isEmpty()) {
                addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]约束[" + constraint + "]格式错误");
                continue;
            }

            if (constraintTypes.contains(constraintType)) {
                addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]有重复约束类型:" + constraintType);
            } else {
                constraintTypes.add(constraintType);
            }

            if (constraintValue.isEmpty()) {
                addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]约束[" + constraintType + "]不能为空值");
            }

            switch (constraintType) {
                case "ref":
                    fieldDefinition.setRef(constraintValue);
                    break;
                case "index":
                    fieldDefinition.setIndex(constraintValue);
                    break;
                case "optional":
                    fieldDefinition.setOptional(constraintValue);
                    break;
                case "lang":
                    fieldDefinition.setLanguage(constraintValue);
                default:
                    addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]不支持该约束类型:" + constraintType);
                    break;
            }
        }
    }

}
