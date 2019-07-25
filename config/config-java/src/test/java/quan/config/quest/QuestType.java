package quan.config.quest;

/**
 * 任务类型<br/>
 * Created by 自动生成
 */
public enum QuestType {

    main(1),//主线
    branch(2);//支线

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

    @Override
    public String toString() {
        return name();
    }
}
