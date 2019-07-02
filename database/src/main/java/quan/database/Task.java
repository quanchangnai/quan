package quan.database;

import java.util.Objects;

/**
 * 事务中执行的任务
 * Created by quanchangnai on 2019/7/1.
 */
@FunctionalInterface
public interface Task {

    Executor executor = null;

    /**
     * 运行事务逻辑
     *
     * @return true:执行成功，false:执行失败
     */
    boolean run();

    /**
     * 在默认执行器里执行任务
     */
    default void execute() {
        Objects.requireNonNull("未设置默认执行器");
        executor.execute(this);
    }

}
