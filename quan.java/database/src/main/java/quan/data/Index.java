package quan.data;

import java.lang.annotation.*;

@Repeatable(Index.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Index {

    /**
     * 索引名
     */
    String name();

    /**
     * 索引字段
     */
    String[] fields();

    /**
     * 索引类型：唯一索引、普通索引或者文本索引
     */
    Type type();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface List {
        Index[] value();
    }

    enum Type {
        NORMAL, UNIQUE, TEXT
    }

}