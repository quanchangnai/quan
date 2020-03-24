package quan.config.item;

import com.alibaba.fastjson.*;
import quan.config.*;

/**
 * 奖励<br/>
 * 自动生成
 */
public class Reward extends Bean {

    protected final int itemId;

    protected final int itemNum;


    public Reward(JSONObject json) {
        super(json);

        this.itemId = json.getIntValue("itemId");
        this.itemNum = json.getIntValue("itemNum");
    }

    public final int getItemId() {
        return itemId;
    }

    public final int getItemNum() {
        return itemNum;
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
