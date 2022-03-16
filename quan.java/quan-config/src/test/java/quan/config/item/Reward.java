package quan.config.item;

import com.alibaba.fastjson.*;
import quan.config.*;
import quan.config.loader.ConfigLoader;

/**
 * 奖励<br/>
 * 代码自动生成，请勿手动修改
 */
public class Reward extends Bean {

    public final int itemId;

    /**
     * 
     */
    public final ItemConfig itemIdRef() {
        return ItemConfig.get(itemId);
    }

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
