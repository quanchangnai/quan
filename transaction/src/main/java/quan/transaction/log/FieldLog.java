package quan.transaction.log;

import quan.transaction.field.TypeField;

/**
 * Created by quanchangnai on 2019/5/16.
 */
public interface FieldLog extends Log {

    TypeField getField();

}
