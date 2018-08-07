package quan.mongo;

/**
 * 引用数据
 * Created by quanchangnai on 2018/8/6.
 */
public abstract class ReferenceData implements Data, UpdateCallback {

    /**
     * 所属的MappingData，当前值
     */
    protected MappingData currentMappingData;

    /**
     * 所属的MappingData，原始值
     */
    protected MappingData originMappingData;

    /**
     * 不要手动调用
     *
     * @param mappingData
     */
    public void setMappingData(MappingData mappingData) {
        this.currentMappingData = mappingData;
    }

    @Override
    public MappingData getMappingData() {
        return currentMappingData;
    }

}
