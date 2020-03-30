package quan.config.item;

/**
 * 道具ID<br/>
 * 自动生成
 */
public enum ItemIds {

    /**
     * 道具1
     */
    item1,

    /**
     * 道具2
     */
    item2;

    public int value() {
        return ItemConfig.getByKey(name()).getId();
    }

}
