package quan.database;

import java.util.List;
import java.util.Map;

/**
 * 数据，对应一张表，每个实例对应表中的一行
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
    public abstract String _name();

    /**
     * 数据主键(_id)
     */
    public abstract I _id();

    /**
     * 数据索引
     */
    public abstract Map<String, Index> _indexes();

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
