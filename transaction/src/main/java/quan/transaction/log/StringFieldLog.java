package quan.transaction.log;

import quan.transaction.field.StringField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class StringFieldLog implements FieldLog {

    private StringField field;

    private String value;

    public StringFieldLog(StringField field, String value) {
        this.field = field;
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public StringField getField() {
        return field;
    }

    @Override
    public void commit() {
        field.setValue(value);
    }

}
