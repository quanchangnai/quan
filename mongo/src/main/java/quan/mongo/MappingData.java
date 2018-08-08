package quan.mongo;

/**
 * 映射数据
 * Created by quanchangnai on 2018/8/6.
 */
public abstract class MappingData extends Data {

    @Override
    protected MappingData getOwner() {
        return this;
    }

    /**
     * 映射的集合名称
     *
     * @return
     */
    public String collection() {
        return getClass().getName();
    }

    /**
     * 普通索引
     *
     * @return
     */
    public String[] indexes() {
        return new String[0];
    }

    /**
     * 唯一索引
     *
     * @return
     */
    public String[] uniques() {
        return new String[0];
    }

}
