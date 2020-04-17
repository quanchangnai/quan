package quan.database.log;

import quan.database.field.Field;

/**
 * Created by quanchangnai on 2019/8/29.
 */
public class ListLog {

    private int modCount;

    public ListLog(Field field, Object value) {
    }

    public int getModCount() {
        return modCount;
    }

    public void incModCount() {
        modCount++;
    }
}
