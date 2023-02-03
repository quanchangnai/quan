package quan.definition;

import org.apache.commons.lang3.StringUtils;
import quan.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 索引定义
 */
public class IndexDefinition extends Definition implements Cloneable {

    private static Set<String> CONFIG_LEGAL_TYPES = CollectionUtils.asSet("normal", "n", "unique", "u");

    private static Set<String> DATA_LEGAL_TYPES = CollectionUtils.asSet("normal", "n", "unique", "u", "text", "t");

    private String ownerName;

    private BeanDefinition ownerDefinition;

    //索引类型
    private String type;

    //被索引的字段名
    private String fieldNames;

    //被索引的字段
    private List<FieldDefinition> fields = new ArrayList<>();

    public IndexDefinition() {
    }

    public IndexDefinition(FieldDefinition field) {
        this.parser = field.getParser();
        this.category = field.getCategory();
        setName(field.getName());
        this.fieldNames = field.getName();
        this.type = field.getIndex();
        if (field.getComment() == null) {
            setComment(field.getColumn());
        } else {
            setComment(field.getComment());
        }
    }

    @Override
    public int getKind() {
        return KIND_INDEX;
    }

    @Override
    public String getKindName() {
        return "索引";
    }

    @Override
    public Pattern getNamePattern() {
        return FieldDefinition.NAME_PATTERN;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public BeanDefinition getOwnerDefinition() {
        return ownerDefinition;
    }

    public IndexDefinition setOwnerDefinition(BeanDefinition ownerDefinition) {
        this.ownerDefinition = ownerDefinition;
        this.ownerName = ownerDefinition.getName();
        return this;
    }

    public boolean isUnique() {
        return isUnique(type);
    }

    public static boolean isUnique(String index) {
        return !StringUtils.isBlank(index) && (index.trim().equals("unique") || index.trim().equals("u"));
    }

    public boolean isNormal() {
        return isNormal(type);
    }

    public static boolean isNormal(String index) {
        return !StringUtils.isBlank(index) && (index.trim().equals("normal") || index.trim().equals("n"));
    }

    public boolean isText() {
        return isText(type);
    }

    public static boolean isText(String index) {
        return !StringUtils.isBlank(index) && (index.trim().equals("text") || index.trim().equals("t"));
    }

    public static boolean isIndex(String index, Category category) {
        boolean result = isUnique(index) || isNormal(index);
        if (category == Category.data) {
            result = result || isText(index);
        }
        return result;
    }

    public static Set<String> getLegalTypes(Category category) {
        if (category == Category.config) {
            return CONFIG_LEGAL_TYPES;
        } else if (category == Category.data) {
            return DATA_LEGAL_TYPES;
        } else {
            return Collections.emptySet();
        }
    }


    public String getType() {
        return type;
    }

    public IndexDefinition setType(String type) {
        if (!StringUtils.isBlank(type)) {
            this.type = type.trim();
        }
        return this;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public boolean addField(FieldDefinition fieldDefinition) {
        if (fields.contains(fieldDefinition)) {
            return false;
        }
        return fields.add(fieldDefinition);
    }

    public String getFieldNames() {
        return fieldNames;
    }

    public IndexDefinition setFieldNames(String fieldNames) {
        if (StringUtils.isBlank(fieldNames)) {
            return this;
        }
        this.fieldNames = fieldNames.trim();
        return this;
    }

    /**
     * 索引方法的后缀
     */
    public String getSuffix() {
        String name = getName();
        if (name.equals("id")) {
            return "";
        }
        return "By" + name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }

    @Override
    public IndexDefinition clone() {
        try {
            return (IndexDefinition) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void validate(List<IndexDefinition> indexes, List<IndexDefinition> selfIndexes, List<FieldDefinition> fields) {
        List<IndexDefinition> fieldIndexes = new ArrayList<>();
        for (FieldDefinition field : fields) {
            if (StringUtils.isBlank(field.getIndex())) {
                continue;
            }
            if (!isIndex(field.getIndex(), field.category)) {
                field.getOwner().addValidatedError(field.getOwner().getValidatedName("的") + field.getValidatedName() + "索引类型[" + field.getIndex() + "]不合法,允许类型" + getLegalTypes(field.category));
                continue;
            }
            if (!field.isPrimitiveType() && !field.isEnumType() && field.getType() != null) {
                field.getOwner().addValidatedError(field.getOwner().getValidatedName("的") + field.getValidatedName() + "类型[" + field.getType() + "]不支持索引，允许的类型为" + Constants.PRIMITIVE_TYPES + "或枚举");
                continue;
            }

            IndexDefinition indexDefinition = new IndexDefinition(field);
            indexDefinition.setOwnerDefinition((BeanDefinition) field.getOwner());
            fieldIndexes.add(indexDefinition);
        }

        indexes.addAll(0, fieldIndexes);
        if (indexes != selfIndexes) {
            selfIndexes.addAll(0, fieldIndexes);
        }

        selfIndexes.forEach(IndexDefinition::validate);

        Set<String> indexNames = new HashSet<>();
        for (IndexDefinition indexDefinition : indexes) {
            if (indexDefinition.getName() == null) {
                continue;
            }
            if (indexNames.contains(indexDefinition.getName())) {
                indexDefinition.getOwnerDefinition().addValidatedError(indexDefinition.getOwnerDefinition().getValidatedName() + "的索引名[" + indexDefinition.getName() + "]重复");
                continue;
            }
            indexNames.add(indexDefinition.getName());
        }

    }

    private void validate() {
        if (getName() == null) {
            ownerDefinition.addValidatedError(ownerDefinition.getValidatedName() + "的索引名不能为空");
        } else if (!getNamePattern().matcher(getName()).matches()) {
            ownerDefinition.addValidatedError(ownerDefinition.getValidatedName() + "的索引名[" + getName() + "]格式错误,正确格式:" + getNamePattern());
        }


        String indexType = getType();
        if (indexType == null) {
            ownerDefinition.addValidatedError(ownerDefinition.getValidatedName("的") + getValidatedName() + "类型不能为空");
        } else {
            Set<String> legalTypes = getLegalTypes(category);
            if (!legalTypes.contains(indexType)) {
                ownerDefinition.addValidatedError(ownerDefinition.getValidatedName("的") + getValidatedName() + "类型[" + indexType + "]非法,允许类型" + legalTypes);
            }
        }

        String fieldNames = getFieldNames();
        if (fieldNames == null) {
            ownerDefinition.addValidatedError(ownerDefinition.getValidatedName("的") + getValidatedName() + "字段不能为空");
            return;
        }

        String[] fieldNameArray = fieldNames.split("[,，]", -1);

        boolean fieldNamePatternError = false;
        for (String fieldName : fieldNameArray) {
            if (!fieldNamePatternError) {
                fieldNamePatternError = StringUtils.isBlank(fieldName);
            }
        }

        if (fieldNamePatternError) {
            ownerDefinition.addValidatedError(ownerDefinition.getValidatedName("的") + getValidatedName() + "字段[" + fieldNames + "]格式错误");
        } else if (fieldNameArray.length > 3) {
            ownerDefinition.addValidatedError(ownerDefinition.getValidatedName("的") + getValidatedName() + "字段[" + fieldNames + "]不能超过三个");
        }

        for (String fieldName : fieldNameArray) {
            if (StringUtils.isBlank(fieldName)) {
                continue;
            }
            FieldDefinition fieldDefinition = ownerDefinition.nameFields.get(fieldName);
            if (fieldDefinition == null) {
                ownerDefinition.addValidatedError(ownerDefinition.getValidatedName("的") + getValidatedName() + "字段[" + fieldName + "]不存在");
                continue;
            }
            if (!fieldDefinition.isPrimitiveType() && !fieldDefinition.isEnumType() && fieldDefinition.getType() != null) {
                ownerDefinition.addValidatedError(ownerDefinition.getValidatedName("的") + getValidatedName() + "字段[" + fieldName + "]类型[" + fieldDefinition.getType() + "]非法，允许的类型为" + Constants.PRIMITIVE_TYPES + "或枚举");
            }
            if (!addField(fieldDefinition)) {
                ownerDefinition.addValidatedError(ownerDefinition.getValidatedName("的") + getValidatedName() + "字段[" + fieldNames + "]不能重复");
            }
        }

    }

}
