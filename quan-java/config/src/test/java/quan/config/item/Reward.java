package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* 奖励<br/>
* Created by 自动生成
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



    @Override
    public String toString() {
        return "Reward{" +
                "itemId=" + itemId +
                ",itemNum=" + itemNum +
                '}';

    }


}
