package quan.mongo;

/**
 * 引用数据，对应MongoDB的一个内嵌文档
 * Created by quanchangnai on 2018/8/6.
 */
public abstract class ReferenceData extends Data {

    /**
     * 当前拥有者
     */
    private MappingData currentOwner;

    /**
     * 原始拥有者
     */
    private MappingData originOwner;

    protected void setOwner(MappingData owner) {
        this.currentOwner = owner;
    }

    @Override
    protected MappingData getOwner() {
        return currentOwner;
    }

    /**
     * 提交数据
     */
    protected void commit() {
        originOwner = currentOwner;
    }

    /**
     * 回滚数据
     */
    protected void rollback() {
        currentOwner = originOwner;
    }


}
