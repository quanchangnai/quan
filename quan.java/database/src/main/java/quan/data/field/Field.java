package quan.data.field;

/**
 * 数据字段
 * Created by quanchangnai on 2019/6/22.
 */
public interface Field {

    /**
     * 提交日志
     *
     * @param log
     */
    void commit(Object log);

}
