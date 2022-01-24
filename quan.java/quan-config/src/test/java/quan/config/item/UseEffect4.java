package quan.config.item;

import com.alibaba.fastjson.*;
import java.util.*;

/**
 * 使用效果4<br/>
 * 代码自动生成，请勿手动修改
 */
public class UseEffect4 extends UseEffect {

    protected final ItemType itemType;


    public UseEffect4(JSONObject json) {
        super(json);

        this.itemType = ItemType.valueOf(json.getIntValue("itemType"));
    }

    public final ItemType getItemType() {
        return itemType;
    }

    public final List<ItemConfig> getItemType$Ref() {
        return ItemConfig.getByType(itemType);
    }


    public static UseEffect4 create(JSONObject json) {
        return new UseEffect4(json);
    }

    @Override
    public String toString() {
        return "UseEffect4{" +
                "aaa=" + aaa +
                ",itemType=" + itemType +
                '}';

    }

}
