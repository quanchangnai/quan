package quan.config.quest;

/**
 * 任务类型<br/>
 * Created by 自动生成
 */
public enum QuestType {

    /**
     * 主线
     */
    main(1),

    /**
     * 支线
     */
    branch(2);


    private int value;

    QuestType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static QuestType valueOf(int value) {
        switch (value) {
            case 1:
                return main;
            case 2:
                return branch;
            default:
                return null;
        }
    }

}
