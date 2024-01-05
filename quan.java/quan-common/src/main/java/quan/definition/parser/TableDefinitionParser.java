package quan.definition.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.Element;
import quan.definition.ClassDefinition;
import quan.definition.Constants;
import quan.definition.FieldDefinition;
import quan.definition.Language;
import quan.definition.config.ConfigDefinition;
import quan.definition.config.ConstantDefinition;
import quan.util.CollectionUtils;
import quan.util.FileUtils;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于表格的【定义】解析器，在表格中直接定义配置，支持使用xml扩展复杂结构
 */
public abstract class TableDefinitionParser extends DefinitionParser {

    public static final int MIN_TABLE_BODY_START_ROW = 4;

    private static Pattern constraintsPattern = Pattern.compile("[\\w:.]+(\\s*;\\s*\\w+(\\s*=\\s*\\w+)?)*(\\s*\\{(.+)})?");

    private Map<String, String> languageAliases = new HashMap<>();

    private Map<String, String> aliasLanguages = new HashMap<>();

    private static Set<String> illegalLanguageAlias = CollectionUtils.asSet(Constants.CONFIG_BUILTIN_TYPES, "i", "I", "index", "u", "U", "unique", "o", "O", "optional", "ref", "lang", "min", "max");

    private ExtDefinitionParser extDefinitionParser = new ExtDefinitionParser();

    /**
     * 主表路径对应分表路径
     */
    private Map<String, Set<String>> mainSubTables = new HashMap<>();

    /**
     * 表名(相对根路径):配置定义
     */
    private Map<String, ConfigDefinition> configDefinitions = new HashMap<>();

    /**
     * 表名(相对根路径):配置定义
     */
    private Map<String, ConfigDefinition> extConfigDefinitions = new HashMap<>();

    public Map<String, String> getLanguageAliases() {
        return languageAliases;
    }

    @Override
    public int getMinTableBodyStartRow() {
        return MIN_TABLE_BODY_START_ROW;
    }


    public void checkLanguageAlias() {
        for (String language : languageAliases.keySet()) {
            String alias = languageAliases.get(language);
            if (illegalLanguageAlias.contains(alias) || !Pattern.matches("\\w", alias) || Language.names().contains(alias) && !alias.equals(language)) {
                throw new IllegalArgumentException(String.format("配置的自定义语言[%s]别名[%s]不合法", language, alias));
            }
            aliasLanguages.put(alias, language);
        }

        if (languageAliases.size() != new HashSet<>(languageAliases.values()).size()) {
            throw new IllegalArgumentException("配置的自定义语言别名有重复：" + languageAliases);
        }
    }

    @Override
    protected boolean checkFile(File definitionFile) {
        if (extDefinitionParser.checkFile(definitionFile)) {
            return true;
        }
        return super.checkFile(definitionFile);
    }

    @Override
    protected void parseFile(File definitionFile) {
        if (extDefinitionParser.checkFile(definitionFile)) {
            extDefinitionParser.setCategory(getCategory());
            extDefinitionParser.parseFile(definitionFile);
            return;
        }

        //表名相对配置表格根路径
        String definitionFilePath = definitionFilePaths.get(definitionFile);
        String definitionFileName = definitionFilePath.substring(0, definitionFilePath.lastIndexOf("."));

        //分表
        if (definitionFileName.contains("-")) {
            String mainTableName = definitionFileName.substring(0, definitionFileName.indexOf("-")).trim();
            mainSubTables.computeIfAbsent(mainTableName, k -> new LinkedHashSet<>()).add(definitionFileName);
            return;
        }

        ConfigDefinition configDefinition = new ConfigDefinition();
        configDefinition.setParser(this);
        configDefinition.setDefinitionFile(definitionFilePath);
        configDefinition.setVersion(String.valueOf(definitionFile.lastModified()));

        String s = File.separator;
        if (definitionFileName.contains(s)) {
            String packageName = definitionFileName.substring(0, definitionFileName.lastIndexOf(s)).replaceAll(String.format("\\%s", s), ".");
            configDefinition.setPackageName(packageName);
            if (!Constants.LOWER_PACKAGE_NAME_PATTERN.matcher(packageName).matches()) {
                addValidatedError("定义文件[" + definitionFilePath + "]的路径格式错误");
            }
            configDefinition.setName(definitionFileName.substring(definitionFileName.lastIndexOf(s) + 1));
        } else {
            configDefinition.setName(definitionFileName);
        }

        configDefinition.setTable(definitionFileName);

        if (parseTable(configDefinition, definitionFile)) {
            parsedClasses.add(configDefinition);
            configDefinitions.put(definitionFileName, configDefinition);
        }
    }

    protected abstract boolean parseTable(ConfigDefinition configDefinition, File definitionFile);

    protected void addField(ConfigDefinition configDefinition, String fieldName, String constraints, String comment) {
        fieldName = fieldName.trim();
        constraints = constraints.trim();
        comment = comment.trim();

        if (fieldName.startsWith("#")) {
            //忽略被注释掉的字段
            return;
        }

        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setParser(this);
        fieldDefinition.setCategory(getCategory());
        fieldDefinition.setName(fieldName);
        fieldDefinition.setColumn(fieldName);
        fieldDefinition.setComment(comment);
        configDefinition.addField(fieldDefinition);

        if (StringUtils.isBlank(constraints)) {
            addValidatedError(configDefinition.getValidatedName("的") + fieldDefinition.getValidatedName() + "约束不能为空，至少要包含字段类型");
            return;
        }

        String originalConstraints = constraints;
        constraints = escapeConstraints(constraints);

        Matcher matcher = constraintsPattern.matcher(constraints);
        if (!matcher.matches()) {
            addValidatedError(configDefinition.getValidatedName("的") + fieldDefinition.getValidatedName() + "约束[" + originalConstraints + "]格式错误");
            return;
        }

        String validation = matcher.group(4);
        if (!StringUtils.isBlank(validation)) {
            fieldDefinition.setValidation(validation);
            constraints = constraints.substring(0, matcher.start(3));
        }

        String[] constraintArray = constraints.split(";", -1);

        fieldDefinition.setTypeInfo(constraintArray[0]);

        Set<String> constraintTypes = new HashSet<>();

        for (int i = 1; i < constraintArray.length; i++) {
            String constraint = constraintArray[i].trim();
            String constraintType;
            String constraintValue;

            String language = aliasLanguages.get(constraint);
            if (language != null) {
                constraint = language;
            }

            switch (constraint) {
                case "i":
                case "I":
                case "index":
                    constraintType = "index";
                    constraintValue = "normal";
                    break;
                case "u":
                case "U":
                case "unique":
                    constraintType = "index";
                    constraintValue = "unique";
                    break;
                case "o":
                case "O":
                case "optional":
                    constraintType = "optional";
                    constraintValue = "true";
                    break;
                default:
                    if (Language.names().contains(constraint)) {
                        constraintType = "lang";
                        constraintValue = constraint;
                    } else {
                        try {
                            String[] constraintTypeAndValue = constraint.split("=", -1);
                            Validate.isTrue(constraintTypeAndValue.length == 2);
                            constraintType = constraintTypeAndValue[0].trim();
                            constraintValue = constraintTypeAndValue[1].trim();
                            Validate.isTrue(!StringUtils.isBlank(constraintType) && !StringUtils.isBlank(constraintValue));
                        } catch (Exception ignored) {
                            addValidatedError(configDefinition.getValidatedName() + "的字段[" + fieldName + "]约束[" + constraint + "]错误");
                            continue;
                        }
                    }
                    break;
            }

            if (constraintTypes.contains(constraintType)) {
                addValidatedError(configDefinition.getValidatedName() + "的字段[" + fieldName + "]有重复约束类型:" + constraintType);
            } else {
                constraintTypes.add(constraintType);
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
                    fieldDefinition.setLanguageStr(constraintValue);
                    break;
                case "min":
                    fieldDefinition.setMin(constraintValue);
                    break;
                case "max":
                    fieldDefinition.setMax(constraintValue);
                    break;
                default:
                    addValidatedError(configDefinition.getValidatedName() + "的字段[" + fieldName + "]不支持该约束类型:" + constraintType);
                    break;
            }
        }
    }

    private static String escapeConstraints(String constraints) {
        StringBuilder sb = new StringBuilder();
        boolean escape = true;

        for (int i = 0; i < constraints.length(); i++) {
            char c = constraints.charAt(i);
            if (escape) {
                if (c == '；') {
                    sb.append(';');
                } else if (c == '：') {
                    sb.append(':');
                } else if (c == '（') {
                    sb.append('(');
                } else if (c == '）') {
                    sb.append(')');
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }

            if ((c == '\'' || c == '"') && i > 1 && constraints.charAt(i - 1) != '\\') {
                //字符串不转义
                escape = !escape;
            }
        }

        return sb.toString();
    }

    @Override
    protected void validate() {
        getValidatedErrors().addAll(extDefinitionParser.getValidatedErrors());
        validateExtDefinitions();

        validateClassName();

        for (String mainTableName : mainSubTables.keySet()) {
            ConfigDefinition configDefinition = configDefinitions.get(mainTableName);
            if (configDefinition != null) {
                String table = mainTableName + "," + String.join(",", mainSubTables.get(mainTableName));
                configDefinition.setTable(table);
            }
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

            String tableName = extConfigDefinition.getTable();
            if (tableName == null) {
                addValidatedError("定义文件[" + extConfigDefinition.getDefinitionFile() + "]中的" + extConfigDefinition.getValidatedName() + "表格名不能为空");
                continue;
            }

            if (extConfigDefinitions.put(tableName, extConfigDefinition) != null) {
                addValidatedError(String.format("相同表格[%s]不能有多个配置定义", tableName));
            }
        }

        if (extConfigDefinitions.isEmpty()) {
            return;
        }

        for (ConfigDefinition extConfigDefinition : extConfigDefinitions.values()) {
            String extTableName = extConfigDefinition.getTable();
            ConfigDefinition configDefinition = configDefinitions.get(FileUtils.toPlatPath(extTableName));
            if (configDefinition == null) {
                addValidatedError(String.format("%s对应的的表格[%s]不存在", extConfigDefinition.getValidatedName(), extTableName));
                continue;
            }

            configDefinition.setPackageName(extConfigDefinition.getPackageName());

            if (!extConfigDefinition.getPackageNames().isEmpty()) {
                configDefinition.getPackageNames().putAll(extConfigDefinition.getPackageNames());
            }

            configDefinition.setName(extConfigDefinition.getName());
            configDefinition.setParentName(extConfigDefinition.getParentName());
            configDefinition.setLanguageStr(extConfigDefinition.getLanguageStr());
            configDefinition.setComment(extConfigDefinition.getComment());
            configDefinition.setVersion(extConfigDefinition.getVersion() + configDefinition.getVersion());

            configDefinition.getValidations().addAll(extConfigDefinition.getValidations());
            extConfigDefinition.getIndexes().forEach(configDefinition::addIndex);

            for (ConstantDefinition constantDefinition : extConfigDefinition.getConstantDefinitions()) {
                constantDefinition.setOwnerDefinition(configDefinition);
            }
        }

    }

    @Override
    public void clear() {
        super.clear();
        extDefinitionParser.clear();
        this.configDefinitions.clear();
        this.mainSubTables.clear();
        this.extConfigDefinitions.clear();
    }

    /**
     * 扩展定义解析器，解析使用XML定义的复杂结构
     */
    private class ExtDefinitionParser extends XmlDefinitionParser {

        public ExtDefinitionParser() {
            super(TableDefinitionParser.this.category);
        }

        @Override
        protected DefinitionParser getDefinitionParser() {
            return TableDefinitionParser.this;
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
