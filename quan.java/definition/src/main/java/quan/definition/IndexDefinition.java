package quan.definition;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 索引定义
 * Created by quanchangnai on 2019/7/14.
 */
public class IndexDefinition extends Definition {

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
    public int getDefinitionType() {
        return 7;
    }

    @Override
    public String getDefinitionTypeName() {
        return "索引";
    }

    @Override
    public Pattern namePattern() {
        return Constants.FIELD_NAME_PATTERN;
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

    public static boolean isIndex(String index) {
        return isUnique(index) || isNormal(index);
    }


    public String getType() {
        return type;
    }

    public IndexDefinition setType(String type) {
        if (isNormal(type) || isUnique(type)) {
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


    public static void validateIndex(BeanDefinition owner, List<IndexDefinition> indexes, List<IndexDefinition> selfIndexes, List<FieldDefinition> fields) {
        for (FieldDefinition field : fields) {
            if (!IndexDefinition.isIndex(field.getIndex())) {
                continue;
            }

            if (!field.isPrimitiveType() && !field.isEnumType() && field.getType() != null) {
                owner.addValidatedError(owner.getValidatedName("的") + field.getValidatedName() + "类型[" + field.getType() + "]不支持索引，允许的类型为" + Constants.PRIMITIVE_TYPES + "或枚举");
                continue;
            }

            IndexDefinition indexDefinition = new IndexDefinition(field);
            indexes.add(indexDefinition);
            if (indexes != selfIndexes) {
                selfIndexes.add(indexDefinition);
            }
        }

        selfIndexes.forEach(index -> index.validateIndex(owner));

        Set<String> indexNames = new HashSet<>();
        for (IndexDefinition indexDefinition : indexes) {
            if (indexDefinition.getName() == null) {
                continue;
            }
            if (indexNames.contains(indexDefinition.getName())) {
                owner.addValidatedError(owner.getValidatedName() + "的索引名[" + indexDefinition.getName() + "]重复");
                continue;
            }
            indexNames.add(indexDefinition.getName());
        }

    }

    private void validateIndex(BeanDefinition owner) {
        if (getName() == null) {
            owner.addValidatedError(owner.getValidatedName() + "的索引名不能为空");
        } else if (!namePattern().matcher(getName()).matches()) {
            owner.addValidatedError(owner.getValidatedName("的") + "索引名[" + getName() + "]格式错误,正确格式:" + namePattern());
        }


        String indexType = getType();
        if (indexType == null) {
            owner.addValidatedError(owner.getValidatedName("的") + getValidatedName() + "类型不能为空");
        } else {
            List<String> allowIndexTypes = Arrays.asList("normal", "n", "unique", "u");
            if (!allowIndexTypes.contains(indexType)) {
                owner.addValidatedError(owner.getValidatedName("的") + getValidatedName() + "类型[" + indexType + "]非法,允许类型" + allowIndexTypes);
            }
        }

        String fieldNames = getFieldNames();
        if (fieldNames == null) {
            owner.addValidatedError(owner.getValidatedName("的") + getValidatedName() + "字段不能为空");
            return;
        }

        String[] fieldNameArray = fieldNames.split(",", -1);

        boolean fieldNamePatternError = false;
        for (String fieldName : fieldNameArray) {
            if (!fieldNamePatternError) {
                fieldNamePatternError = StringUtils.isBlank(fieldName);
            }
        }

        if (fieldNamePatternError) {
            owner.addValidatedError(owner.getValidatedName("的") + getValidatedName() + "字段[" + fieldNames + "]格式错误");
        } else if (fieldNameArray.length > 3) {
            owner.addValidatedError(owner.getValidatedName("的") + getValidatedName() + "字段[" + fieldNames + "]不能超过三个");
        }

        for (String fieldName : fieldNameArray) {
            if (StringUtils.isBlank(fieldName)) {
                continue;
            }
            FieldDefinition fieldDefinition = owner.nameFields.get(fieldName);
            if (fieldDefinition == null) {
                owner.addValidatedError(owner.getValidatedName("的") + getValidatedName() + "字段[" + fieldName + "]不存在");
                continue;
            }
            if (!fieldDefinition.isPrimitiveType() && !fieldDefinition.isEnumType() && fieldDefinition.getType() != null) {
                owner.addValidatedError(owner.getValidatedName("的") + getValidatedName() + "字段[" + fieldName + "]类型[" + fieldDefinition.getType() + "]非法，允许的类型为" + Constants.PRIMITIVE_TYPES + "或枚举");
            }
            if (!addField(fieldDefinition)) {
                owner.addValidatedError(owner.getValidatedName("的") + getValidatedName() + "字段[" + fieldNames + "]不能重复");
            }
        }

    }

}
