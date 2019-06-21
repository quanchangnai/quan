package quan.database.log;

import quan.database.field.Field;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public interface FieldLog extends Log {

    Field getField();

}
