package quan.database;

/**
 * 事务执行成功或失败之后需要执行的任务
 * Created by quanchangnai on 2019/7/1.
 */
@FunctionalInterface
public interface AfterTask extends Runnable {

    default void onException(Exception e) {
        e.printStackTrace();
    }

}
