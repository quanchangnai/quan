package quan.mongo;

import org.bson.Document;

/**
 * 映射数据，映射到MongoDB集合的一个顶层文档，相当于关系型数据库表的一行
 * Created by quanchangnai on 2018/8/6.
 */
public abstract class MappingData extends Data {

    /**
     * 当前状态
     */
    private int state;

    /**
     * 新建状态
     */
    public static final int NEW_STATE = 0;

    /**
     * 解码状态
     */
    public static final int DECODING_STATE = 1;

    /**
     * 正常状态
     */
    public static final int NORMAL_STATE = 2;


    /**
     * 是否处于新建状态，新建状态下事务提交时执行插入操作
     *
     * @return
     */
    public boolean isNewState() {
        return state == NEW_STATE;
    }

    /**
     * 是否处于解码状态，解码时不需要在事务中
     *
     * @return
     */
    public boolean isDecodingState() {
        return state == DECODING_STATE;
    }

    /**
     * 是否处于正常状态，正常状态下事务提交时执行更新操作
     *
     * @return
     */
    public boolean isNormalState() {
        return state == NORMAL_STATE;
    }

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

    /**
     * 编码
     *
     * @return
     */
    public Document encode() {
        state = NORMAL_STATE;
        return doEncode();
    }

    /**
     * 解码
     *
     * @param document
     */
    public void decode(Document document) {
        state = DECODING_STATE;
        doDecode(document);
        state = NORMAL_STATE;
    }


}
