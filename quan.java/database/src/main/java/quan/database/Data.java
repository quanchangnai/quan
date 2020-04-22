package quan.database;

import java.util.List;

/**
 * 数据对应一张表，每个实例对应表中的一行
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class Data<I> {

    /**
     * 主键(_id)
     */
    public static final String _ID = "_id";

    /**
     * 对应的表名
     */
    public abstract String _name();

    /**
     * 主键(_id)
     */
    public abstract I _id();

    /**
     * 索引
     */
    public abstract List<Index> _indexes();

    /**
     * 更新器
     */
    private volatile DataUpdater updater;

    public final DataUpdater _getUpdater() {
        return updater;
    }

    /**
     * 使用指定的更新器在提交事务后更新数据
     */
    public final void update(DataUpdater updater) {
        this.updater = updater;
    }

    /**
     * 数据索引
     */
    public static class Index {

        private String name;

        //索引字段
        private List<String> fields;

        //唯一索引或者普通索引
        private boolean unique;

        public Index(String name, List<String> fields, boolean unique) {
            this.name = name;
            this.fields = fields;
            this.unique = unique;
        }

        public String getName() {
            return name;
        }

        public List<String> getFields() {
            return fields;
        }

        public boolean isUnique() {
            return unique;
        }

    }

}
