package quan.config;

import quan.config.item.EquipConfig;
import quan.config.item.ItemConfig;
import quan.config.item.WeaponConfig;
import quan.config.quest.QuestConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigTest {

    public static void main(String[] args) throws Exception {
        List<String> definitionPaths = new ArrayList<>();
        definitionPaths.add("generator\\definition\\config");

//        String tableType = "csv";
//        String tablePath = "config\\csv";
        String tableType = "xlsx";
        String tablePath = "config\\excel";
//        String tableType = "json";
//        String tablePath = "config\\json";


        ConfigLoader configLoader = new ConfigLoader(tablePath);
        configLoader.useXmlDefinition(definitionPaths, "quan.config");
//        configLoader.notUseDefinition("quan.config");
        configLoader.setLoadType(LoadType.validateAndLoad);
        configLoader.setValidatorsPackage("quan");
        configLoader.setTableType(tableType);

        loadConfig(configLoader);

        writeJson(configLoader, false);

        reloadAllConfig(configLoader);

        reloadByConfigName(configLoader);

        reloadByTableName(configLoader);

        reloadByOriginalName(configLoader);

    }

    private static void loadConfig(ConfigLoader configLoader) throws Exception {
        System.err.println("configLoader.loadConfig()=============");
        long startTime = System.currentTimeMillis();
        try {
            configLoader.load();
        } catch (ValidatedException e) {
            printErrors(e);
        }
        printConfig();
        System.err.println("configLoader.loadConfig()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void writeJson(ConfigLoader configLoader, boolean useSimpleName) {
        if (configLoader.getTableType().equals("json")) {
            return;
        }
        System.err.println("writeJson()=============");
        long startTime = System.currentTimeMillis();
        configLoader.writeJson("config\\json", useSimpleName);
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
        Thread.sleep(5000);
        List<String> reloadConfigs = Arrays.asList("ItemConfig", "WeaponConfig");
        System.err.println("reloadByTableName()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();
        try {
            configLoader.reloadByTableName(reloadConfigs);
        } catch (ValidatedException e) {
            printErrors(e);
        }
        printConfig();
        System.err.println("reloadByTableName()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void reloadByOriginalName(ConfigLoader configLoader) throws Exception {
        Thread.sleep(5000);
        List<String> reloadConfigs = Arrays.asList("道具", "武器");
        System.err.println("reloadByOriginalName()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();
        try {
            configLoader.reloadByOriginalName(reloadConfigs);
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
