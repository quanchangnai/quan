package quan.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据库
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class Database {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 默认数据库实例
     */
    private static Database instance;

    private static AtomicInteger nextId = new AtomicInteger();

    /**
     * 数据库实例ID，正常情况下只会有一个数据库实例
     */
    private int id;


    public Database() {

    }


    protected abstract void open0();

    public static Database getDefault() {
        return instance;
    }

    public static void setDefault(Database database) {
        Database.instance = database;
    }


    /**
     * 关闭数据库时会存档后清空缓存，但未结束的事务会执行失败
     */
    public synchronized void close() {

    }


}
