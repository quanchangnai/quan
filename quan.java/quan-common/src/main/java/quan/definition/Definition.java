package quan.definition;

import org.apache.commons.lang3.StringUtils;
import quan.definition.parser.DefinitionParser;

import java.util.regex.Pattern;

/**
 * 各种【定义】的抽象类
 */
public abstract class Definition {

    protected Category category;

    private String name;

    protected String underscoreName;

    private String comment = "";

    protected DefinitionParser parser;

    public Definition() {
    }

    public Category getCategory() {
        return category;
    }

    public Definition setCategory(Category category) {
        this.category = category;
        return this;
    }

    public void setParser(DefinitionParser parser) {
        this.parser = parser;
    }

    public DefinitionParser getParser() {
        return parser;
    }

    public String getName() {
        return name;
    }

    public String getValidatedName() {
        return getValidatedName("");
    }

    public String getValidatedName(String append) {
        String kindName = getKindName();
        String validatedName;
        if (name != null) {
            validatedName = kindName + "[" + name + "]" + append;
        } else {
            validatedName = kindName + append;
        }
        return validatedName;
    }

    public void setName(String name) {
        if (!StringUtils.isBlank(name)) {
            this.name = name.trim();
        }
    }

    public String getUnderscoreName() {
        return underscoreName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (!StringUtils.isBlank(comment)) {
            this.comment = comment.trim();
        }
    }

    public abstract int getKind();

    public abstract String getKindName();

    public abstract Pattern getNamePattern();

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name='" + name + '\'' +
                '}';
    }

}

