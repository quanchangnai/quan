package quan.database.field;

import quan.database.Bean;
import quan.database.Data;
import quan.database.util.Validations;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public class BeanField<T extends Bean> extends BaseField<T> {

    public BeanField() {
    }

    public BeanField(T value) {
        super(value);
    }

    public void setLogValue(T value, Data root) {
        Validations.validFieldValue(value);

        T oldValue = getValue();
        if (oldValue != null) {
            oldValue.setLogRoot(null);
        }
        if (value != null) {
            value.setLogRoot(root);
        }

        super.setLogValue(value, root);

    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}
