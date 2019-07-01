package quan.database;

/**
 * 事务执行之后(失败或回滚)需要执行的任务
 * Created by quanchangnai on 2019/7/1.
 */
@FunctionalInterface
public interface AfterTask {

    void run();

    default void onException(Exception e) {
        e.printStackTrace();
    }

}
