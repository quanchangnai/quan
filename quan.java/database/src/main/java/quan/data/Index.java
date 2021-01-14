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
     * 唯一索引或者普通索引
     */
    boolean unique();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface List {
        Index[] value();
    }

}