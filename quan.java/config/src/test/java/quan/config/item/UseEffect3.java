package quan.config.item;

import com.alibaba.fastjson.*;

/**
 * 使用效果3<br/>
 * 自动生成
 */
public class UseEffect3 extends UseEffect {

    protected final int ccc;


    public UseEffect3(JSONObject json) {
        super(json);

        this.ccc = json.getIntValue("ccc");
    }

    public final int getCcc() {
        return ccc;
    }


    public static UseEffect3 create(JSONObject json) {
        return new UseEffect3(json);
    }

    @Override
    public String toString() {
        return "UseEffect3{" +
                "aaa=" + aaa +
                ",ccc=" + ccc +
                '}';

    }

}
