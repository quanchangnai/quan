package quan.definition;

import org.apache.commons.lang3.StringUtils;
import quan.definition.parser.DefinitionParser;

import java.util.regex.Pattern;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Definition {

    protected Category category;

    private String name;

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
        String definitionTypeName = getDefinitionTypeName();
        String validatedName;
        if (name != null) {
            validatedName = definitionTypeName + "[" + name + "]" + append;
        } else {
            validatedName = definitionTypeName + append;
        }
        return validatedName;
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

    protected abstract Pattern namePattern();

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name='" + name + '\'' +
                '}';
    }
}

