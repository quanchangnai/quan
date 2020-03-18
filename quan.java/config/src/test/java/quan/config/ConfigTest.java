package quan.config;

import quan.config.item.EquipConfig;
import quan.config.item.ItemConfig;
import quan.config.item.WeaponConfig;
import quan.config.quest.QuestConfig;
import quan.definition.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigTest {

    public static void main(String[] args) throws Exception {

        ConfigLoader configLoader;
        if (true) {
            configLoader = withDefinitionConfigLoader();
        } else {
            configLoader = withoutDefinitionConfigLoader();
        }

        loadConfig(configLoader);

        writeJson(configLoader);

//        reloadAllConfig(configLoader);
//
//        reloadByConfigName(configLoader);
//
//        reloadByTableName(configLoader);

//        reloadByOriginalName(configLoader);

    }

    private static ConfigLoader withDefinitionConfigLoader() {
        List<String> definitionPaths = new ArrayList<>();
        definitionPaths.add("definition\\config");

//        TableType tableType = TableType.csv;
//        String tablePath = "config\\csv";
        TableType tableType = TableType.xlsx;
        String tablePath = "config\\excel";

        WithDefinitionConfigLoader configLoader = new WithDefinitionConfigLoader(tablePath);
        configLoader.useXmlDefinition(definitionPaths, "quan.config");
        configLoader.setValidatorsPackage("quan");
        configLoader.setTableType(tableType);

        return configLoader;
    }

    private static ConfigLoader withoutDefinitionConfigLoader() {
        String tablePath = "config\\json";
        WithoutDefinitionConfigLoader configLoader = new WithoutDefinitionConfigLoader(tablePath);
        configLoader.setValidatorsPackage("quan");
        configLoader.setPackagePrefix("quan.config");
        return configLoader;
    }

    private static void loadConfig(ConfigLoader configLoader) throws Exception {
        System.err.println("configLoader.loadConfig()=============");
        long startTime = System.currentTimeMillis();
        try {
            configLoader.loadAll();
        } catch (ValidatedException e) {
            printErrors(e);
        }
        printConfig();
        System.err.println("configLoader.loadConfig()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void writeJson(ConfigLoader configLoader) {
        if (configLoader.getTableType() == TableType.json) {
            return;
        }
        if (!(configLoader instanceof WithDefinitionConfigLoader)) {
            return;
        }

        WithDefinitionConfigLoader configLoader1 = (WithDefinitionConfigLoader) configLoader;

        System.err.println("writeJson()=============");
        long startTime = System.currentTimeMillis();
        configLoader1.writeJson("config\\json", true, Language.java);
        System.err.println("writeJson()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void reloadAllConfig(ConfigLoader configLoader) throws Exception {
        Thread.sleep(10000);
        System.err.println("reloadAllConfig()=============");
        long startTime = System.currentTimeMillis();
        try {
            configLoader.reloadAll();
        } catch (ValidatedException e) {
            printErrors(e);
        }
        printConfig();
        System.err.println("reloadAllConfig()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void reloadByConfigName(ConfigLoader configLoader) throws Exception {
        Thread.sleep(5000);
        List<String> reloadConfigs = Arrays.asList("ItemConfig", "WeaponConfig");
        System.err.println("reloadByConfigName()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();
        try {
            configLoader.reloadByConfigName(reloadConfigs);
        } catch (ValidatedException e) {
            printErrors(e);
        }
        printConfig();
        System.err.println("reloadByConfigName()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void reloadByTableName(ConfigLoader configLoader) throws Exception {
        if (!(configLoader instanceof WithDefinitionConfigLoader)) {
            return;
        }

        WithDefinitionConfigLoader configLoader1 = (WithDefinitionConfigLoader) configLoader;

        Thread.sleep(5000);
        List<String> reloadConfigs = Arrays.asList("道具", "装备1");
        System.err.println("reloadByTableName()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();
        try {
            configLoader1.reloadByTableName(reloadConfigs);
        } catch (ValidatedException e) {
            printErrors(e);
        }
        printConfig();
        System.err.println("reloadByTableName()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void reloadByOriginalName(ConfigLoader configLoader) throws Exception {
        if (!(configLoader instanceof WithDefinitionConfigLoader)) {
            return;
        }

        WithDefinitionConfigLoader configLoader1 = (WithDefinitionConfigLoader) configLoader;
        Thread.sleep(5000);
        List<String> reloadConfigs = Arrays.asList("道具", "武器");
        System.err.println("reloadByOriginalName()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();
        try {
            configLoader1.reloadByOriginalName(reloadConfigs);
        } catch (ValidatedException e) {
            printErrors(e);
        }
        printConfig();
        System.err.println("reloadByOriginalName()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void printErrors(ValidatedException e) {
        System.err.println("printErrors start============");
        for (String error : e.getErrors()) {
            System.err.println(error);
        }
        System.err.println("printErrors end============");
    }

    private static void printConfig() throws Exception {
        System.err.println("printConfigs start============");
        Thread.sleep(100);
        System.err.println("ItemConfig============");
        ItemConfig.getConfigs().forEach(System.out::println);

        System.err.println("EquipConfig============");
        EquipConfig.self.getConfigs().forEach(System.out::println);

        System.err.println("WeaponConfig============");
        WeaponConfig.self.getConfigs().forEach(System.out::println);

        System.err.println("QuestConfig============");
        QuestConfig.getConfigs().forEach(System.out::println);

        System.err.println("printConfigs end============");
    }
}
