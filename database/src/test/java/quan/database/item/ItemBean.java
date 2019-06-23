package quan.database.item;

import quan.database.Bean;
import java.util.*;
import quan.database.field.*;

/**
 * 道具
 * Created by 自动生成
 */
public class ItemBean extends Bean {

    private BaseField<Integer> id = new BaseField<>(0);

    private BaseField<String> name = new BaseField<>("");

    public int getId() {
        return id.getValue();
    }

    public void setId(int id) {
        this.id.setLogValue(id, getRoot());
    }

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setLogValue(name, getRoot());
    }

    @Override
    public String toString() {
        return "ItemBean{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }

}
