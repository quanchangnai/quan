package quan.transaction.field;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class StringField implements Field {

    private String value;

    public StringField() {
    }

    public StringField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
