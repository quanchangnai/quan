package quan.generator;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Definition {

    private Category category;

    private String name;

    private String comment = "";

    public Category getCategory() {
        return category;
    }

    public Definition setCategory(Category category) {
        this.category = category;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getName4Validate() {
        return getName4Validate("");
    }

    public String getName4Validate(String append) {
        String definitionTypeName = getDefinitionTypeName();
        String name4Validate;
        if (name != null) {
            name4Validate = definitionTypeName + "[" + name + "]" + append;
        } else {
            name4Validate = definitionTypeName + append;
        }
        return name4Validate;
    }


    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        this.name = name.trim();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (StringUtils.isBlank(comment)) {
            return;
        }
        this.comment = comment.trim();
    }

    public abstract int getDefinitionType();

    public abstract String getDefinitionTypeName();

    /**
     * 定义类别
     */
    public enum Category {
        data,
        message,
        config;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name='" + name + '\'' +
                '}';
    }
}

