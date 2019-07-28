package quan.config.quest;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class QuestTargetConfig extends Config {

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


    //ID
	private static Map<Long, QuestTargetConfig> idConfigs = new HashMap<>();


    public static Map<Long, QuestTargetConfig> getIdConfigs() {
        return idConfigs;
    }

    public static QuestTargetConfig getById(long id) {
        return idConfigs.get(id);
    }


    public static void index(List<QuestTargetConfig> configs) {
        Map<Long, QuestTargetConfig> idConfigs = new HashMap<>();

        for (QuestTargetConfig config : configs) {
            if (idConfigs.put(config.id, config) != null) {
                throw new RuntimeException("配置[QuestTargetConfig]的索引[id]:[" + config.id + "]有重复");
            }
        }

        QuestTargetConfig.idConfigs = unmodifiable(idConfigs);

    }

}
