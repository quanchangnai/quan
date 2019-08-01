package quan.generator;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Definition {

    private String name;

    private String comment = "";

    public String getName() {
        return name;
    }

    public String getName4Validate() {
        return getName4Validate("");
    }

    public String getName4Validate(String append) {
        if (name != null) {
            return "[" + name + "]" + append;
        }
        return "";
    }


    public void setName(String name) {
        if (name == null || name.trim().equals("")) {
            return;
        }
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (comment == null || comment.trim().equals("")) {
            return;
        }
        this.comment = comment;
    }

    public abstract int getDefinitionType();

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name='" + name + '\'' +
                '}';
    }
}

