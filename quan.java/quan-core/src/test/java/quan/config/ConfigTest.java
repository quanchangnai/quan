package quan.config;

import quan.config.item.EquipConfig;
import quan.config.item.ItemConfig;
import quan.config.item.WeaponConfig;
import quan.config.loader.ConfigLoader;
import quan.config.loader.DefinitionConfigLoader;
import quan.config.loader.JsonConfigLoader;
import quan.config.quest.QuestConfig;
import quan.definition.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigTest {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        ConfigLoader configLoader;
        if (args.length > 0 && args[0].equals("1")) {
            configLoader = jsonConfigLoader();
        } else {
            configLoader = definitionConfigLoader();
        }

        loadConfig(configLoader);

        writeJson(configLoader);

        reloadAllConfig(configLoader);

        reloadByConfigName(configLoader);

        reloadByTableName(configLoader);

        reloadByOriginalName(configLoader);

        System.err.println("ConfigTest.main()耗时:" + (System.currentTimeMillis() - startTime) / 1000D + "s");
    }


    private static ConfigLoader definitionConfigLoader() {
        List<String> definitionPaths = new ArrayList<>();
        definitionPaths.add("quan-core\\definition\\config");

//        TableType tableType = TableType.csv;
//        String tablePath = "config\\csv";
        TableType tableType = TableType.xlsx;
        String tablePath = "quan-core\\config\\excel";

        DefinitionConfigLoader configLoader = new DefinitionConfigLoader(tablePath);
        configLoader.useXmlDefinition(definitionPaths, "quan.config");
        configLoader.setValidatorsPackage("quan");
        configLoader.setTableType(tableType);

        return configLoader;
    }

    private static ConfigLoader jsonConfigLoader() {
        String tablePath = "quan-core\\config\\json";
        JsonConfigLoader configLoader = new JsonConfigLoader(tablePath);
        configLoader.setValidatorsPackage("quan");
        configLoader.setPackagePrefix("quan.config");
        return configLoader;
    }

    private static void loadConfig(ConfigLoader configLoader) {
        System.err.println("configLoader.loadConfig()=============");
        long startTime = System.currentTimeMillis();

        try {
            configLoader.loadAll();
        } catch (ValidatedException e) {
            printErrors(e);
        }
        printConfig();

        System.err.println("configLoader.loadConfig()耗时:" + (System.currentTimeMillis() - startTime) / 1000D + "s");
        System.err.println();
    }

    private static void writeJson(ConfigLoader configLoader) {
        if (configLoader.getTableType() == TableType.json || !(configLoader instanceof DefinitionConfigLoader)) {
            return;
        }

        DefinitionConfigLoader configLoader1 = (DefinitionConfigLoader) configLoader;

        System.err.println("writeJson()=============");
        long startTime = System.currentTimeMillis();

        configLoader1.writeJson("quan-core\\config\\json", true, Language.cs);

        System.err.println("writeJson()耗时:" + (System.currentTimeMillis() - startTime) / 1000D + "s");
        System.err.println();
    }

    private static void reloadAllConfig(ConfigLoader configLoader) {
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

    private static void reloadByConfigName(ConfigLoader configLoader) {
        List<String> reloadConfigs = Arrays.asList("item.ItemConfig", "WeaponConfig");
        System.err.println("reloadByConfigName()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();

        try {
            configLoader.reloadByConfigName(reloadConfigs);
        } catch (ValidatedException e) {
            printErrors(e);
        }
        printConfig();

        System.err.println("reloadByConfigName()耗时:" + (System.currentTimeMillis() - startTime) / 1000D + "s");
        System.err.println();
    }

    private static void reloadByTableName(ConfigLoader configLoader) {
        if (!(configLoader instanceof DefinitionConfigLoader)) {
            return;
        }

        DefinitionConfigLoader configLoader1 = (DefinitionConfigLoader) configLoader;
        List<String> reloadConfigs = Arrays.asList("道具/道具", "装备1");
        System.err.println("reloadByTableName()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();

        try {
            configLoader1.reloadByTableName(reloadConfigs);
        } catch (ValidatedException e) {
            printErrors(e);
        }
        printConfig();

        System.err.println("reloadByTableName()耗时:" + (System.currentTimeMillis() - startTime) / 1000D + "s");
        System.err.println();
    }

    private static void reloadByOriginalName(ConfigLoader configLoader) {
        if (!(configLoader instanceof DefinitionConfigLoader)) {
            return;
        }

        DefinitionConfigLoader configLoader1 = (DefinitionConfigLoader) configLoader;
        List<String> reloadConfigs = Arrays.asList("道具", "道具/武器");
        System.err.println("reloadByOriginalName()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();

        try {
            configLoader1.reloadByOriginalName(reloadConfigs);
        } catch (ValidatedException e) {
            printErrors(e);
        }
        printConfig();

        System.err.println("reloadByOriginalName()耗时:" + (System.currentTimeMillis() - startTime) / 1000D + "s");
        System.err.println();
    }

    private static void printErrors(ValidatedException e) {
        System.err.println();
        System.err.println("printErrors start============");

        for (String error : e.getErrors()) {
            System.err.println(error);
        }

        System.err.println("printErrors end============");
        System.err.println();
    }

    private static void printConfig() {
        System.err.println();
        System.err.println("printConfigs start============");

        System.err.println("ItemConfig============");
        ItemConfig.getConfigs().forEach(System.err::println);

        System.err.println("EquipConfig============");
        EquipConfig.self.getConfigs().forEach(System.err::println);

        System.err.println("WeaponConfig============");
        WeaponConfig.self.getConfigs().forEach(System.err::println);

        System.err.println("QuestConfig============");
        QuestConfig.getConfigs().forEach(System.err::println);

        System.err.println("CardConfig============");
        CardConfig.getConfigs().forEach(System.err::println);

        System.err.println("printConfigs end============");
    }

}
