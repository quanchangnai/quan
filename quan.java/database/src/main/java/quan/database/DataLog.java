package quan.database;

import java.util.Objects;

/**
 * 记录数据的创建删除
 * Created by quanchangnai on 2019/6/24.
 */
@SuppressWarnings({"unchecked"})
class DataLog {

    private Key key;

    /**
     * 事务里的当前数据
     */
    private Data current;

    /**
     * 缓存表里的原始数据行
     */
    private Table.Row originRow;

    /**
     * 缓存里的原始数据
     */
    private Data originData;

    /**
     * 原始数据的状态
     */
    private int originState;

    /**
     * 记录之前没有查询的空删除
     */
    private boolean deleted;

    public DataLog(Data current, Table.Row originRow, Data originData, int originState, Table table, Object key) {
        this.key = new Key(table, key);
        this.current = current;
        this.originRow = originRow;
        this.originData = originData;
        this.originState = originState;
    }

    public DataLog setDeleted() {
        deleted = true;
        current = null;
        return this;
    }

    public Key getKey() {
        return key;
    }


    public Data getCurrent() {
        if (current != null) {
            current.touch();
        }
        return current;
    }

    public DataLog setCurrent(Data current) {
        current.touch();
        this.current = current;
        this.deleted = false;

        return this;
    }

    public Table getTable() {
        return key.table;
    }

    public boolean isConflict() {
        key.table.checkWorkable();

        //有可能出现事务执行时间比缓存的过期时间还长的极端情况
        long costTime = System.currentTimeMillis() - Transaction.get(true).getTaskStartTime();
        if (costTime > getTable().getCacheExpire() * 1000) {
            return true;
        }

        Table.Row row = key.table.getRow(key.k);
        if (originRow != row) {
            return true;
        }

        //这里同时读row的data和state不需要加锁，是因为已经加上了行级锁
        return row != null && (originData != row.getData() || originState != row.getState());
    }

    public void commit() {
        if (deleted && originState != Table.Row.DELETE) {
            key.table.setDelete(key.k);
        }
        if (current != null && (originRow == null || originState == Table.Row.DELETE)) {
            key.table.setInsert(current);
        }
    }

    public static class Key {

        private Table table;

        private Object k;

        public Key(Table table, Object k) {
            this.table = table;
            this.k = k;
        }

        public Object getK() {
            return k;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key1 = (Key) o;
            return Objects.equals(table, key1.table) &&
                    Objects.equals(k, key1.k);
        }

        @Override
        public int hashCode() {
            return Objects.hash(table, k);
        }

    }

}
