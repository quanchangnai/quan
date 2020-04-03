package quan.database;

/**
 * 数据
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class Data<I> extends Entity {

    public static final String _ID = "_id";

    @Override
    protected final Data<I> _getRoot() {
        return this;
    }

    public abstract I _getId();

}
