package quan.mongo;

import org.bson.Document;

/**
 * Mongo操作模板
 * Created by quanchangnai on 2018/8/9.
 */
public class MongoTemplate {

    /**
     * 插入文档
     *
     * @param clazz
     * @param data
     */
    public void insert(Class<? extends MappingData> clazz, Document data) {
        System.err.println("插入文档，clazz：" + clazz);
        System.err.println(data);
    }

    /**
     * 删除文档
     *
     * @param clazz
     * @param filter
     * @return
     */
    public void remove(Class<? extends MappingData> clazz, Document filter) {
    }

    /**
     * 删除指定ID的文档
     *
     * @param clazz
     * @param id
     */
    public void remove(Class<? extends MappingData> clazz, Object id) {
        Document filter = new Document();
        filter.put("_id", id);
        remove(clazz, filter);
    }

    /**
     * 更新文档
     *
     * @param clazz
     * @param data
     */
    public void update(Class<? extends MappingData> clazz, Document data) {
        System.err.println("更新文档，clazz：" + clazz);
        System.err.println(data);
    }

    /**
     * 查找文档
     *
     * @param clazz
     * @param filter
     * @return
     */
    public Document find(Class<? extends MappingData> clazz, Document filter) {
        return null;
    }


}
