package quan.database.field;

import quan.database.Bean;
import quan.database.Data;

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
        T oldValue = getValue();

        super.setLogValue(value, root);

        if (oldValue != null) {
            oldValue.setLogRoot(null);
        }
        if (value != null) {
            value.setLogRoot(root);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}
