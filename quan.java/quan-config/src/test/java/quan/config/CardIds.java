package quan.config;

/**
 * 卡片ID<br/>
 * 代码自动生成，请勿手动修改
 */
public enum CardIds {

    /**
     * 卡片1
     */
    card1,

    /**
     * 卡片2
     */
    card2;

    public int value() {
        return CardConfig.getByKey(name()).id;
    }

}
