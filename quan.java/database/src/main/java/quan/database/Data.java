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

    DataWriter writer;

    State state;

    /**
     * 表名
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

    private void setLog(DataWriter updater, State state) {
        Transaction transaction = Transaction.get(true);
        Log log = transaction.getDataLog(this);
        if (log == null) {
            log = new Log(updater, state);
            transaction.setDataLog(this, log);
        } else {
            log.writer = updater;
            log.state = state;
        }
    }

    /**
     * 使用指定的更新器在提交事务后插入数据
     */
    public final void insert(DataWriter updater) {
        setLog(updater, State.INSERTION);
    }

    /**
     * 使用指定的更新器在提交事务后更新数据
     */
    public final void update(DataWriter updater) {
        setLog(updater, State.UPDATE);
    }

    /**
     * 使用指定的更新器在提交事务后删除数据
     */
    public final void delete(DataWriter updater) {
        setLog(updater, State.DELETION);
    }

    void commit(Log log) {
        this.writer = log.writer;
        this.state = State.UPDATE;
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

    static class Log {

        DataWriter writer;

        State state;

        public Log(DataWriter writer, State state) {
            this.writer = writer;
            this.state = state;
        }
    }

    /**
     * 数据的状态,代表事务提交后对数据执行什么操作
     */
    enum State {
        INSERTION, UPDATE, DELETION
    }

}
