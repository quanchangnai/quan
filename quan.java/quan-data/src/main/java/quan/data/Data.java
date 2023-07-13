package quan.data;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * 数据类对应一张表，每个数据实例对应表中的一行
 */
public abstract class Data<I> implements Entity {

    /**
     * 主键(_id)
     */
    public static final String _ID = "_id";

    private static DataAccessor defaultAccessor;

    DataAccessor accessor;

    State state;

    /**
     * 修改过的字段编号
     */
    protected final BitSet _updatedFields = new BitSet();

    public static DataAccessor _getDefaultAccessor() {
        return defaultAccessor;
    }

    public static void _setDefaultAccessor(DataAccessor accessor) {
        Data.defaultAccessor = accessor;
    }

    public static String name(Class<? extends Data<?>> clazz) {
        try {
            return (String) clazz.getField("_NAME").get(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 主键ID
     */
    public abstract I id();

    /**
     * 不在事务中需要设置存取器时通过反射引用此方法
     */
    @SuppressWarnings("unused")
    private static final BiConsumer<Data<?>, DataAccessor> _setAccessor = (data, accessor) -> {
        data.accessor = accessor;
        data.state = State.UPDATE;
    };


    private void _setAccessor(Transaction transaction, DataAccessor accessor, State state) {
        State oldState = this.state;
        Log log = transaction.getDataLog(this);
        if (log != null) {
            oldState = log.state;
        }

        if (oldState == State.UPDATE && state == State.INSERT || (oldState == State.INSERT || oldState == State.DELETE) && state == State.UPDATE) {
            throw new IllegalStateException(String.format("当前状态[%s]下的数据[%s(%s])不支持%s操作", oldState, this.getClass().getName(), id(), state));
        }

        if (oldState == State.INSERT && state == State.DELETE || oldState == State.DELETE && state == State.INSERT) {
            State prevState = this.state;
            Log prevDataLog = transaction.getPrevDataLog(this, log);
            if (prevDataLog != null) {
                prevState = prevDataLog.state;
            }
            state = prevState;
        }

        if (log == null) {
            transaction.setDataLog(this, accessor, state);
        } else {
            log.accessor = accessor;
            log.state = state;
        }
    }

    public DataAccessor _getAccessor() {
        if (accessor == null) {
            return defaultAccessor;
        }
        return accessor;
    }

    /**
     * 更新补丁，字段名:字段值
     */
    protected Map<String, Object> _getUpdatePatch() {
        return null;
    }

    /**
     * 使用指定的存取器插入数据，在内存事务中操作将会在提交时真正执行
     */
    public final void insert(DataAccessor accessor) {
        Objects.requireNonNull(accessor, "参数[accessor]不能为空");

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setAccessor(transaction, accessor, State.INSERT);
        } else if (Transaction.isOptional()) {
            this.accessor = accessor;
            accessor.write(Collections.singleton(this), null, null);
        } else {
            Validations.transactionError();
        }
    }

    /**
     * 使用缓存下来或者默认的存取器插入数据
     *
     * @see #insert(DataAccessor)
     */
    public final void insert() {
        insert(_getAccessor());
    }

    /**
     * 使用指定的存取器更新数据，在内存事务中操作将会在提交时真正执行
     */
    public final void update(DataAccessor accessor) {
        Objects.requireNonNull(accessor, "参数[accessor]不能为空");

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setAccessor(transaction, accessor, State.UPDATE);
        } else if (Transaction.isOptional()) {
            this.accessor = accessor;
            Map<String, Object> patch = _getUpdatePatch();
            if (patch != null) {
                _updatedFields.clear();
                Map<Data<?>, Map<String, Object>> updates = new HashMap<>();
                updates.put(this, patch);
                accessor.write(null, null, updates);
            }
        } else {
            Validations.transactionError();
        }
    }

    /**
     * 使用缓存下来或者默认的存取器更新数据，在内存事务中设置数据字段时会自动更新
     *
     * @see #update(DataAccessor)
     */
    public final void update() {
        update(_getAccessor());
    }

    /**
     * 使用指定的存取器删除数据，在内存事务中操作将会在提交时真正执行
     */
    public final void delete(DataAccessor accessor) {
        Objects.requireNonNull(accessor, "参数[accessor]不能为空");

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setAccessor(transaction, accessor, State.DELETE);
        } else if (Transaction.isOptional()) {
            this.accessor = accessor;
            accessor.write(null, Collections.singleton(this), null);
        } else {
            Validations.transactionError();
        }
    }

    /**
     * 使用缓存下来或者默认的存取器删除数据
     *
     * @see #delete(DataAccessor)
     */
    public final void delete() {
        delete(_getAccessor());
    }

    /**
     * 清除设置的存取器和状态
     */
    public final void free() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setAccessor(transaction, null, null);
        } else if (Transaction.isOptional()) {
            this.accessor = null;
            this.state = null;
        } else {
            Validations.transactionError();
        }
    }

    /**
     * 提交日志中记录的存取器
     */
    void commit(Log log) {
        if (log.state == State.DELETE) {
            this.accessor = null;
            this.state = null;
            this._updatedFields.clear();
        } else {
            this.accessor = log.accessor;
            this.state = State.UPDATE;
            this._updatedFields.or(log.updatedFields);
        }
    }

    static class Log {

        /**
         * @see Data#accessor
         */
        DataAccessor accessor;

        /**
         * @see Data#state
         */
        State state;

        /**
         * @see Data#_updatedFields
         */
        final BitSet updatedFields = new BitSet();

        public Log(DataAccessor accessor, State state) {
            this.accessor = accessor;
            this.state = state;
        }
    }

    /**
     * 数据的状态，代表事务提交后对数据执行什么操作，从数据库查询出来时会自动设置为{@link #UPDATE}状态
     */
    enum State {
        INSERT, UPDATE, DELETE
    }

}
