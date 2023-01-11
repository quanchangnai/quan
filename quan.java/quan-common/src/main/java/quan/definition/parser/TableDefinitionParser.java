package quan.definition.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.Element;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.config.ConfigDefinition;
import quan.definition.config.ConstantDefinition;

import java.io.File;
import java.util.*;

/**
 * 基于表格的【定义】解析器，在表格中直接定义配置，支持使用xml扩展复杂结构
 */
public abstract class TableDefinitionParser extends DefinitionParser {

    private ExtDefinitionParser extDefinitionParser = new ExtDefinitionParser(this);

    private Map<String, Set<String>> subtables = new HashMap<>();

    private Map<String, ConfigDefinition> extConfigDefinitions = new HashMap<>();


    @Override
    public int getMinTableBodyStartRow() {
        return 4;
    }

    @Override
    protected boolean checkFile(File definitionFile) {
        if (extDefinitionParser != null && extDefinitionParser.checkFile(definitionFile)) {
            return true;
        }
        return super.checkFile(definitionFile);
    }

    @Override
    protected void parseFile(File definitionFile) {
        if (extDefinitionParser != null && extDefinitionParser.checkFile(definitionFile)) {
            extDefinitionParser.setCategory(getCategory());
            extDefinitionParser.parseFile(definitionFile);
            return;
        }

        //TODO 目录暂未处理
        String tableName = definitionFile.getName().substring(0, definitionFile.getName().lastIndexOf("."));

        //分表
        if (tableName.contains("-")) {
            //主表
            String mainTableName = tableName.substring(0, tableName.indexOf("-")).trim();
            subtables.computeIfAbsent(mainTableName, k -> new LinkedHashSet<>()).add(tableName);
            return;
        }

        ConfigDefinition configDefinition = new ConfigDefinition();
        configDefinition.setParser(this);
        configDefinition.setDefinitionFile(definitionFile.getName());

        if (tableName.contains(".")) {
            configDefinition.setPackageName(tableName.substring(0, tableName.lastIndexOf(".")));
            configDefinition.setName(tableName.substring(tableName.lastIndexOf(".") + 1));
        } else {
            configDefinition.setName(tableName);
        }

        configDefinition.setTable(configDefinition.getName());

        if (parseTable(configDefinition, definitionFile)) {
            parsedClasses.add(configDefinition);
        }
    }

    protected abstract boolean parseTable(ConfigDefinition configDefinition, File definitionFile);

    protected void addField(ConfigDefinition configDefinition, String columnName, String fieldName, String fieldConstraint) {
        if (columnName.startsWith("#")) {
            //忽略注释列
            return;
        }

        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setParser(this);
        fieldDefinition.setCategory(getCategory());
        fieldDefinition.setName(fieldName);
        fieldDefinition.setColumn(columnName);
        configDefinition.addField(fieldDefinition);

        if (StringUtils.isBlank(fieldConstraint)) {
            addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]约束不能为空，至少要包含字段类型");
            return;
        }

        String[] constraints = fieldConstraint.split("[;；]", -1);

        fieldDefinition.setTypes(constraints[0]);

        Set<String> constraintTypes = new HashSet<>();

        for (int i = 1; i < constraints.length; i++) {
            String constraint = constraints[i].trim();
            String constraintType;
            String constraintValue;

            switch (constraint) {
                case "cs":
                case "lua":
                case "java":
                    constraintType = "lang";
                    constraintValue = constraint;
                    break;
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
                        Validate.isTrue(constraintTypeAndValue.length == 2);
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
                    break;
                default:
                    addValidatedError(configDefinition.getValidatedName() + "的列[" + columnName + "]不支持该约束类型:" + constraintType);
                    break;
            }
        }
    }

    @Override
    protected void validate() {
        if (extDefinitionParser != null) {
            getValidatedErrors().addAll(extDefinitionParser.getValidatedErrors());
            validateExtDefinitions();
        }

        validateClassName();

        for (String mainTableName : subtables.keySet()) {
            ConfigDefinition configDefinition = extConfigDefinitions.get(mainTableName);
            if (configDefinition != null) {
                configDefinition = getConfig(configDefinition.getName());
            } else {
                configDefinition = getConfig(mainTableName);
            }

            String table = mainTableName + "," + String.join(",", subtables.get(mainTableName));
            configDefinition.setTable(table);
        }

        parsedClasses.forEach(ClassDefinition::validate1);
        parsedClasses.forEach(ClassDefinition::validate2);
        parsedClasses.forEach(ClassDefinition::validate3);
    }

    private void validateExtDefinitions() {
        for (ClassDefinition classDefinition : extDefinitionParser.parsedClasses) {
            if (!(classDefinition instanceof ConfigDefinition)) {
                parsedClasses.add(classDefinition);
                continue;
            }

            ConfigDefinition extConfigDefinition = (ConfigDefinition) classDefinition;

            String configName = extConfigDefinition.getName();
            String tableName = extConfigDefinition.getTable();

            if (configName == null) {
                addValidatedError("定义文件[" + extConfigDefinition.getDefinitionFile() + "]中配置的名字不能为空");
                continue;
            }

            if (tableName == null) {
                tableName = configName;
            }

            if (extConfigDefinitions.put(tableName, extConfigDefinition) != null) {
                addValidatedError(String.format("相同表格[%s]不能有多个配置定义", tableName));
            }
        }

        if (extConfigDefinitions.isEmpty()) {
            return;
        }

        Map<String, ConfigDefinition> _extConfigDefinitions = new HashMap<>(extConfigDefinitions);

        for (ClassDefinition classDefinition : parsedClasses) {
            if (!(classDefinition instanceof ConfigDefinition)) {
                continue;
            }

            ConfigDefinition configDefinition = (ConfigDefinition) classDefinition;
            ConfigDefinition extConfigDefinition = _extConfigDefinitions.remove(configDefinition.getTable());
            if (extConfigDefinition == null) {
                continue;
            }

            configDefinition.setPackageName(extConfigDefinition.getPackageName());
            configDefinition.setName(extConfigDefinition.getName());
            configDefinition.setParentName(extConfigDefinition.getParentName());
            configDefinition.setComment(extConfigDefinition.getComment());

            extConfigDefinition.getIndexes().forEach(configDefinition::addIndex);

            for (ConstantDefinition constantDefinition : extConfigDefinition.getConstantDefinitions()) {
                constantDefinition.setOwnerDefinition(configDefinition);
            }
        }

        for (String tableName : _extConfigDefinitions.keySet()) {
            ConfigDefinition extConfigDefinition = _extConfigDefinitions.get(tableName);
            if (extConfigDefinition.getName() != null) {
                addValidatedError(String.format("%s对应的的表格[%s]不存在", extConfigDefinition.getValidatedName(), tableName));
            }
        }
    }


    /**
     * 扩展定义解析器，解析使用XML定义的复杂结构
     */
    private static class ExtDefinitionParser extends XmlDefinitionParser {

        private DefinitionParser definitionParser;


        public ExtDefinitionParser(DefinitionParser definitionParser) {
            super(definitionParser.category);
            this.definitionParser = definitionParser;
        }

        @Override
        protected DefinitionParser getDefinitionParser() {
            return definitionParser;
        }

        @Override
        protected FieldDefinition parseField(ClassDefinition classDefinition, Element fieldElement, int indexInParent) {
            if (classDefinition instanceof ConfigDefinition) {
                //不支持定义字段
                return null;
            } else {
                return super.parseField(classDefinition, fieldElement, indexInParent);
            }
        }

    }

}
