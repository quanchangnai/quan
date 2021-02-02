package quan.data;

import quan.data.Transaction.Listener;
import quan.data.field.Field;

import java.util.*;

/**
 * 保存点，存储外层事务日志，支持内嵌事务独立回滚
 */
class Savepoint {

    //Transaction.failed
    boolean failed;

    //Transaction.dataLogs
    Map<Data<?>, Data.Log> dataLogs = new LinkedHashMap<>();

    //Transaction.rootLogs
    Map<Node, Data<?>> rootLogs = new HashMap<>();

    //Transaction.fieldLogs
    Map<Field, Object> fieldLogs = new HashMap<>();

    //Transaction.listeners
    List<Listener> listeners = new ArrayList<>();

}
