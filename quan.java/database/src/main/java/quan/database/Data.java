package quan.database;

/**
 * 数据，对应表中的一行
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class Data<I> extends Entity {

    /**
     * 主键(_id)
     */
    public static final String _ID = "_id";


    @Override
    protected final Data<I> _getLogRoot() {
        return this;
    }

    /**
     * 数据对应的表名
     */
    public abstract String _getName();

    /**
     * 主键(_id)
     */
    public abstract I _getId();

}
