package quan.mongo;

/**
 * 引用数据
 * Created by quanchangnai on 2018/8/6.
 */
public abstract class ReferenceData implements Data, UpdateCallback {

    /**
     * 所属的MappingData
     */
    private MappingData mappingData;

    protected void setMappingData(MappingData mappingData) {
        this.mappingData = mappingData;
    }

    @Override
    public MappingData getMappingData() {
        return mappingData;
    }

}
