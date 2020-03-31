package quan.database;

/**
 * Created by quanchangnai on 2019/5/16.
 */
@SuppressWarnings("unchecked")
public final class EntityField<V extends Entity> extends BaseField<V> {

    public EntityField() {
    }

    public EntityField(V value) {
        super(value);
    }

    public void setLogValue(V value, Data root) {

    }

}
