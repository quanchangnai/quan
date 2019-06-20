package quan.generator.message;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Definition {

    public static final int DEFINITION_TYPE_ENUM = 1;
    public static final int DEFINITION_TYPE_BEAN = 2;
    public static final int DEFINITION_TYPE_MESSAGE = 3;
    public static final int DEFINITION_TYPE_FIELD = 4;

    private String name;
    private String  comment="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public abstract int getDefinitionType();



}

