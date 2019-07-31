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

    protected int itemId;

    protected int itemNum;

    public int getItemId() {
        return itemId;
    }

    public int getItemNum() {
        return itemNum;
    }


    @Override
    public void parse(JSONObject object) {
        super.parse(object);

        itemId = object.getIntValue("itemId");
        itemNum = object.getIntValue("itemNum");
    }

    @Override
    public String toString() {
        return "Reward{" +
                "itemId=" + itemId +
                ",itemNum=" + itemNum +
                '}';

    }

}
