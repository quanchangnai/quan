package quan.config.item;

import com.alibaba.fastjson.*;

/**
 * 使用效果3<br/>
 * 代码自动生成，请勿手动修改
 */
public class UseEffect3 extends UseEffect {

    public final int ccc;


    public UseEffect3(JSONObject json) {
        super(json);

        this.ccc = json.getIntValue("ccc");
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
