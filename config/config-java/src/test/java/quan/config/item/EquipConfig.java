package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class EquipConfig extends Config {

    //部位
    private int position;

    /**
     * 部位
     */
    public int getPosition() {
        return position;
    }


    @Override
    protected void parse(JSONObject object) {
        position = object.getIntValue("position");
    }

    @Override
    public String toString() {
        return "EquipConfig{" +
                "position=" + position +
                '}';

        }


    //部位
    private static Map<Integer, List<EquipConfig>> positionConfigs = new HashMap<>();


    public static Map<Integer, List<EquipConfig>> getPositionConfigs() {
        return positionConfigs;
    }

    public static List<EquipConfig> getByPosition(int position) {
        return positionConfigs.getOrDefault(position, Collections.emptyList());
    }


    public static void index(List<EquipConfig> configs) {
        Map<Integer, List<EquipConfig>> positionConfigs = new HashMap<>();

        for (EquipConfig config : configs) {
            positionConfigs.computeIfAbsent(config.position, k -> new ArrayList<>()).add(config);
        }

        EquipConfig.positionConfigs = unmodifiable(positionConfigs);

    }

}
