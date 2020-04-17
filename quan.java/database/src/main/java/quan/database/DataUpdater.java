package quan.database;

import java.util.List;

/**
 * 数据更新器，负责在事务提交后更新数据到数据库
 * Created by quanchangnai on 2020/4/17.
 */
public interface DataUpdater {

    void update(List<Data> updates);

}
