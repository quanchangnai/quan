package quan.config.common;

import com.alibaba.fastjson.*;
import quan.config.*;
import quan.config.loader.ConfigLoader;
import quan.config.item.ItemConfig;

/**
 * 奖励<br/>
 * 代码自动生成，请勿手动修改
 */
public class Reward extends Bean {

    public final int itemId;

    public final int itemNum;


    public Reward(JSONObject json) {
        super(json);

        this.itemId = json.getIntValue("itemId");
        this.itemNum = json.getIntValue("itemNum");
    }

    public static Reward create(JSONObject json) {
        return new Reward(json);
    }

    @Override
    public String toString() {
        return "Reward{" +
                "itemId=" + itemId +
                ",itemNum=" + itemNum +
                '}';

    }

}
