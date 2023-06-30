package quan.data;

import quan.data.mongo.DataJsonWriter;

import java.util.Collections;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 数据类对应一张表，每个数据实例对应表中的一行
 */
public abstract class Data<I> {

    /**
     * 主键(_id)
     */
    public static final String _ID = "_id";

    private static DataWriter defaultWriter;

    DataWriter writer;

    State state;

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
        data.state = State.SAVE;
    };

    private void _setWriter(Transaction transaction, DataWriter writer, State state) {
        Log log = transaction.getDataLog(this);
        if (log == null) {
            log = new Log(writer, state);
            transaction.setDataLog(this, log);
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


    /**
     * 使用指定的写入器保存数据，在内存事务中操作将会在提交时真正执行
     */
    public final void save(DataWriter writer) {
        Objects.requireNonNull(writer, "参数[writer]不能为空");

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setWriter(transaction, writer, State.SAVE);
        } else if (Transaction.isOptional()) {
            writer.write(Collections.singleton(this), null);
        } else {
            Validations.transactionError();
        }
    }

    /**
     * 使用缓存下来或者默认的写入器保存数据，在内存事务中设置数据字段时会自动保存
     *
     * @see #save(DataWriter)
     */
    public final void save() {
        save(_getWriter());
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
            writer.write(null, Collections.singleton(this));
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
     * 清除设置的写入器和状态，数据从数据库查询出来时会自动设置
     */
    public final void free() {
        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setWriter(transaction, null, null);
        } else {
            this.writer = null;
            this.state = null;
        }
    }

    public String toJson() {
        try (DataJsonWriter dataJsonWriter = new DataJsonWriter(this)) {
            return dataJsonWriter.toJson();
        }
    }

    /**
     * 提交日志中记录的写入器
     */
    void commit(Log log) {
        if (log.state == State.DELETE) {
            this.writer = null;
            this.state = null;
        } else {
            this.writer = log.writer;
            this.state = State.SAVE;
        }
    }

    /**
     * 数据日志，记录使用的写入器和数据状态
     */
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
        SAVE, DELETE
    }

}
