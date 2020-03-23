package quan.config.item;

import java.util.*;
import com.alibaba.fastjson.*;
import quan.config.*;

/**
 * 使用效果2<br/>
 * 自动生成
 */
public class UseEffect2 extends UseEffect {

    protected final int bbb;


    public UseEffect2(JSONObject json) {
        super(json);

        this.bbb = json.getIntValue("bbb");
    }

    public final int getBbb() {
        return bbb;
    }


    public static UseEffect2 create(JSONObject json) {
        return new UseEffect2(json);
    }

    @Override
    public String toString() {
        return "UseEffect2{" +
                "aaa=" + aaa +
                ",bbb=" + bbb +
                '}';

    }


}
