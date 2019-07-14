package quan.database.item;

/**
 * 道具类型<br/>
 * Created by 自动生成
 */
public enum ItemType {

    type1(1),//道具类型1
    type2(2);//道具类型2

    private int value;

    ItemType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ItemType valueOf(int value) {
        switch (value) {
            case 1:
                return type1;
            case 2:
                return type2;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return name();
    }
}
