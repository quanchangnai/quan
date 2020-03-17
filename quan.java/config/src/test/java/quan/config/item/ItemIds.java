package quan.config.item;

/**
 * 道具ID<br/>
 * 自动生成
 */
public class ItemIds {

    /**
     * 道具1
     */
    public static int item1() {
        return ItemConfig.getByKey("item1").getId();
    }

    /**
     * 道具2
     */
    public static int item2() {
        return ItemConfig.getByKey("item2").getId();
    }

}
