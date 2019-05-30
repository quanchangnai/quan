package quan.transaction.log;

import org.pcollections.PSet;
import org.pcollections.PVector;
import quan.transaction.field.ListField;
import quan.transaction.field.SetField;

import java.util.List;

/**
 * Created by quanchangnai on 2019/5/20.
 */
public class ListLog<E> implements FieldLog {

    private ListField<E> field;

    private PVector<E> data;

    public ListLog(ListField<E> field) {
        this.field = field;
        this.data =  field.getData();
    }

    @Override
    public ListField<E> getField() {
        return field;
    }

    public PVector<E> getData() {
        return data;
    }

    public ListLog<E> setData(PVector<E> data) {
        this.data = data;
        return this;
    }

    @Override
    public void commit() {
        field.setData(data);
    }


}
