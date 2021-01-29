package quan.data;

import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonWriter;
import quan.data.mongo.CodecsRegistry;
import quan.data.mongo.JsonStringWriter;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 数据类对应一张表，每个数据实例对应表中的一行<br/>
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
     * 不在事务中需要设置写入器时通过反射引用此方法
     */
    @SuppressWarnings("unused")
    private static BiConsumer<Data<?>, DataWriter> _setWriter = (data, writer) -> {
        data.writer = writer;
        data.state = State.UPDATE;
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

    /**
     * 使用指定的写入器插入数据，在内存事务中操作将会在提交时真正执行
     */
    public final void insert(DataWriter writer) {
        Objects.requireNonNull(writer, "参数[writer]不能为空");

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setWriter(transaction, writer, State.INSERTION);
        } else if (Transaction.isOptional()) {
            writer.write(Collections.singletonList(this), null, null);
        } else {
            Transaction.error();
        }
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
            writer.write(null, Collections.singletonList(this), null);
        } else {
            Transaction.error();
        }
    }

    /**
     * 使用指定的写入器删除数据，在内存事务中操作将会在提交时真正执行
     */
    public final void delete(DataWriter writer) {
        Objects.requireNonNull(writer, "参数[writer]不能为空");

        Transaction transaction = Transaction.get();
        if (transaction != null) {
            _setWriter(transaction, writer, State.DELETION);
        } else if (Transaction.isOptional()) {
            writer.write(null, null, Collections.singletonList(this));
        } else {
            Transaction.error();
        }
    }

    /**
     * 数据从数据查询出来时，会设置默认写入器和状态(更新)，本操作会清除以上设置
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

    @SuppressWarnings({"unchecked"})
    public String toJson() {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonStringWriter(stringWriter);
        Codec codec = CodecsRegistry.getDefault().get(getClass());
        codec.encode(jsonWriter, this, EncoderContext.builder().build());
        return stringWriter.toString();
    }

    /**
     * 提交日志中记录的写入器
     */
    void commit(Log log) {
        if (log.state == State.DELETION) {
            this.writer = null;
            this.state = null;
        } else {
            this.writer = log.writer;
            this.state = State.UPDATE;
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
        INSERTION, UPDATE, DELETION
    }

}
