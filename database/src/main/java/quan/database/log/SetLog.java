package quan.database.log;

import org.pcollections.PSet;
import quan.database.field.SetField;

/**
 * Created by quanchangnai on 2019/5/20.
 */
public class SetLog<E> implements FieldLog {

    private SetField<E> field;

    private PSet<E> data;

    public SetLog(SetField<E> field) {
        this.field = field;
        this.data =  field.getData();
    }

    @Override
    public SetField<E> getField() {
        return field;
    }

    public PSet<E> getData() {
        return data;
    }

    public SetLog<E> setData(PSet<E> data) {
        this.data = data;
        return this;
    }

    @Override
    public void commit() {
        field.setData(data);
    }


}
