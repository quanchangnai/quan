package quan.data;

import org.bson.Document;

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

    private static DataWriter defaultWriter;

    DataWriter writer;

    State state;

    /**
     * 修改过的字段编号
     */
    protected final BitSet _updatedFields = new BitSet();

    public static DataWriter _getDefaultWriter() {
        return defaultWriter;
    }

    public static void _setDefaultWriter(DataWriter writer) {
        Data.defaultWriter = writer;
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
     * 不在事务中需要设置写入器时通过反射引用此方法
     */
    @SuppressWarnings("unused")
    private static final BiConsumer<Data<?>, DataWriter> _setWriter = (data, writer) -> {
        data.writer = writer;
        data.state = State.UPDATE;
    };


    private void _setWriter(Transaction transaction, DataWriter writer, State state) {
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
            transaction.setDataLog(this, writer, state);
        } else {
            log.writer = writer;
            log.state = state;
        }
    }

    public DataWriter _getWriter() {
        if (writer == null) {
            return defaultWriter;
        }
        return writer;
    }

    protected Document _getUpdatePatch() {
        return null;
    }

    /**
     * 使用指定的写入器插入数据，在内存事务中操作将会在提交时真正执行
     */
    public final void insert(DataWriter writer) {
        Objects.requireNonNull(writer, "参数[writer]不能为空");

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setWriter(transaction, writer, State.INSERT);
        } else if (Transaction.isOptional()) {
            this.writer = writer;
            writer.write(Collections.singleton(this), null, null);
        } else {
            Validations.transactionError();
        }
    }

    /**
     * 使用缓存下来或者默认的写入器插入数据
     *
     * @see #insert(DataWriter)
     */
    public final void insert() {
        insert(_getWriter());
    }

    /**
     * 使用指定的写入器更新数据，在内存事务中操作将会在提交时真正执行
     */
    public final void update(DataWriter writer) {
        Objects.requireNonNull(writer, "参数[writer]不能为空");

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setWriter(transaction, writer, State.UPDATE);
        } else if (Transaction.isOptional()) {
            this.writer = writer;
            Document patch = _getUpdatePatch();
            if (patch != null) {
                _updatedFields.clear();
                Map<Data<?>, Document> updates = new HashMap<>();
                updates.put(this, patch);
                writer.write(null, null, updates);
            }
        } else {
            Validations.transactionError();
        }
    }

    /**
     * 使用缓存下来或者默认的写入器更新数据，在内存事务中设置数据字段时会自动更新
     *
     * @see #update(DataWriter)
     */
    public final void update() {
        update(_getWriter());
    }

    /**
     * 使用指定的写入器删除数据，在内存事务中操作将会在提交时真正执行
     */
    public final void delete(DataWriter writer) {
        Objects.requireNonNull(writer, "参数[writer]不能为空");

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setWriter(transaction, writer, State.DELETE);
        } else if (Transaction.isOptional()) {
            this.writer = writer;
            writer.write(null, Collections.singleton(this), null);
        } else {
            Validations.transactionError();
        }
    }

    /**
     * 使用缓存下来或者默认的写入器删除数据
     *
     * @see #delete(DataWriter)
     */
    public final void delete() {
        delete(_getWriter());
    }

    /**
     * 清除设置的写入器和状态
     */
    public final void free() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setWriter(transaction, null, null);
        } else if (Transaction.isOptional()) {
            this.writer = null;
            this.state = null;
        } else {
            Validations.transactionError();
        }
    }

    /**
     * 提交日志中记录的写入器
     */
    void commit(Log log) {
        if (log.state == State.DELETE) {
            this.writer = null;
            this.state = null;
            this._updatedFields.clear();
        } else {
            this.writer = log.writer;
            this.state = State.UPDATE;
            this._updatedFields.or(log.updatedFields);
        }
    }

    static class Log {

        /**
         * @see Data#writer
         */
        DataWriter writer;

        /**
         * @see Data#state
         */
        State state;

        /**
         * @see Data#_updatedFields
         */
        final BitSet updatedFields = new BitSet();

        public Log(DataWriter writer, State state) {
            this.writer = writer;
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
