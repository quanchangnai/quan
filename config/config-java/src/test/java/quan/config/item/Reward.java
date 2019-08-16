package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
* 奖励<br/>
* Created by 自动生成
*/
@SuppressWarnings({"unchecked"})
public class Reward extends Bean {

    public final int itemId;

    public final int itemNum;


    public Reward(JSONObject json) {
        super(json);

        itemId = json.getIntValue("itemId");
        itemNum = json.getIntValue("itemNum");
    }


    @Override
    public String toString() {
        return "Reward{" +
                "itemId=" + itemId +
                ",itemNum=" + itemNum +
                '}';

    }

}
