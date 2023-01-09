package quan.definition.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.Element;
import quan.definition.ClassDefinition;
import quan.definition.FieldDefinition;
import quan.definition.IndexDefinition;
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

        String tableName = definitionFile.getName().substring(0, definitionFile.getName().lastIndexOf("."));

        //分表
        if (tableName.contains("-")) {
            //主表
            String mainTableName = tableName.substring(0, tableName.indexOf("-")).trim();
            subtables.computeIfAbsent(mainTableName, k -> new LinkedHashSet<>()).add(tableName);
            return;
        }

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
            parsedClasses.addAll(extDefinitionParser.parsedClasses);
        }

        validateClassName();

        if (extDefinitionParser != null) {
            validateExtDefinitions();
        }

        for (String mainTableName : subtables.keySet()) {
            getConfig(mainTableName).setTable(mainTableName + "," + String.join(",", subtables.get(mainTableName)));
        }

        parsedClasses.forEach(ClassDefinition::validate1);
        parsedClasses.forEach(ClassDefinition::validate2);
        parsedClasses.forEach(ClassDefinition::validate3);
    }

    private void validateExtDefinitions() {
        for (IndexDefinition indexDefinition : extDefinitionParser.indexes) {
            ClassDefinition ownerDefinition = getClass(indexDefinition.getOwnerName());
            if (ownerDefinition instanceof ConfigDefinition) {
                ((ConfigDefinition) ownerDefinition).addIndex(indexDefinition);
            } else if (indexDefinition.getOwnerName() == null) {
                addValidatedError(String.format("%s的所属配置不能为空", indexDefinition.getValidatedName()));
            } else {
                addValidatedError(String.format("%s的所属配置[%s]不存在", indexDefinition.getValidatedName(), indexDefinition.getOwnerName()));
            }
        }

        for (ConstantDefinition constantDefinition : extDefinitionParser.constants) {
            ClassDefinition ownerDefinition = getClass(constantDefinition.getOwnerName());
            if (ownerDefinition instanceof ConfigDefinition) {
                constantDefinition.setOwnerDefinition((ConfigDefinition) ownerDefinition);
            } else if (constantDefinition.getOwnerName() == null) {
                addValidatedError(String.format("%s的所属配置不能为空", constantDefinition.getValidatedName()));
            } else {
                addValidatedError(String.format("%s的所属配置[%s]不存在", constantDefinition.getValidatedName(), constantDefinition.getOwnerName()));
            }
        }
    }


    /**
     * 扩展定义解析器，解析使用XML定义的复杂结构
     */
    private static class ExtDefinitionParser extends XmlDefinitionParser {

        private DefinitionParser definitionParser;

        private List<IndexDefinition> indexes = new ArrayList<>();

        private List<ConstantDefinition> constants = new ArrayList<>();


        public ExtDefinitionParser(DefinitionParser definitionParser) {
            super(definitionParser.category);
            this.definitionParser = definitionParser;
        }

        @Override
        protected DefinitionParser getDefinitionParser() {
            return definitionParser;
        }

        @Override
        protected ClassDefinition createClassDefinition(Element element, int index) {
            switch (element.getName()) {
                case "config":
                    throw new IllegalArgumentException();
                case "index": {
                    IndexDefinition indexDefinition = parseIndex(element, null, 0);
                    indexDefinition.setOwnerName(element.attributeValue("config"));
                    indexDefinition.setComment(getComment(element, index));
                    indexes.add(indexDefinition);
                    break;
                }
                case "constant": {
                    ConstantDefinition constantDefinition = parseConstant(element, null, null, 0);
                    constantDefinition.setOwnerName(element.attributeValue("config"));
                    constantDefinition.setComment(getComment(element, index));
                    constants.add(constantDefinition);
                    break;
                }
            }

            return super.createClassDefinition(element, index);
        }

    }

}
