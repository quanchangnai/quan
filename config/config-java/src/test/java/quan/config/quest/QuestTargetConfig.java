package quan.config.quest;

import quan.config.*;
import java.util.*;
import com.alibaba.fastjson.*;

/**
* Created by 自动生成
*/
public class QuestTargetConfig extends Config {

    //ID
	private static Map<Long, QuestTargetConfig> idConfigs = new HashMap<>();

    public static Map<Long, QuestTargetConfig> getById() {
        return idConfigs;
    }

    public static QuestTargetConfig getById(long id) {
        return idConfigs.get(id);
    }


    static void index(List<QuestTargetConfig> configs) {
        Map<Long, QuestTargetConfig> idConfigs = new HashMap<>();

        QuestTargetConfig oldConfig;
        for (QuestTargetConfig config : configs) {
            oldConfig = idConfigs.put(config.id, config);
            if (oldConfig != null) {
                throw new RuntimeException("配置[" + QuestTargetConfig.class.getSimpleName() + "]的索引[id]:[" + config.id + "]有重复");
            }

        }

        QuestTargetConfig.idConfigs = unmodifiable(idConfigs);
    }


    //ID
    private long id;

    //名字
    private String name;


    /**
     * ID
     */
    public long getId() {
        return id;
    }

    /**
     * 名字
     */
    public String getName() {
        return name;
    }


    @Override
    protected void parse(JSONObject object) {
        id = object.getLongValue("id");
        name = object.getString("name");
    }

    @Override
    public String toString() {
        return "QuestTargetConfig{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

        }

}
