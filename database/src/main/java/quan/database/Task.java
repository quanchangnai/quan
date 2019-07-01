package quan.database;

/**
 * 事务中执行的任务
 * Created by quanchangnai on 2019/7/1.
 */
@FunctionalInterface
public interface Task {

    /**
     * @return true:执行成功，false:执行失败
     */
    boolean run();

}
