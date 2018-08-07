package quan.mongo;

/**
 * 映射数据
 * Created by quanchangnai on 2018/8/6.
 */
public abstract class MappingData implements Data, UpdateCallback {

    @Override
    public void setMappingData(MappingData mappingData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MappingData getMappingData() {
        return this;
    }

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


}
