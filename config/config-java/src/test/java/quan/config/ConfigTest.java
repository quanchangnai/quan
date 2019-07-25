package quan.config;

import quan.config.quest.QuestType;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigTest {

    public static void main(String[] args) {

        QuestType questType = QuestType.valueOf("main");
        System.err.println(questType);
    }

}
