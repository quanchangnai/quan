package quan.database.item;

/**
 * 道具类型<br/>
 * 自动生成
 */
public enum ItemType {

    /**
     * 道具类型1
     */
    type1(1),

    /**
     * 道具类型2
     */
    type2(2);


    private int value;

    ItemType(int value) {
        this.value = value;
    }

    public int value() {
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

}
