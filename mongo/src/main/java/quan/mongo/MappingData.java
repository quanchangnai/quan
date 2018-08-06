package quan.mongo;

/**
 * 映射数据
 * Created by quanchangnai on 2018/8/6.
 */
public abstract class MappingData implements Data, UpdateCallback {

    /**
     * 映射的集合名称
     *
     * @return
     */
    public abstract String collection();

    /**
     * 索引
     *
     * @return
     */
    public abstract String[] indexes();

    @Override
    public MappingData getMappingData() {
        return this;
    }

}
