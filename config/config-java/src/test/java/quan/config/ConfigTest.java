package quan.config;

import quan.config.item.EquipConfig;
import quan.config.item.ItemConfig;
import quan.config.item.WeaponConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigTest {

    public static void main(String[] args) throws Exception {
        List<String> definitionPaths = new ArrayList<>();
        definitionPaths.add("generator\\src\\test\\java\\quan\\generator");
        definitionPaths.add("generator\\src/test\\java\\quan\\generator\\config");

//        String tableType = "csv";
//        String tablePath = "config\\csv";
//        String tableType = "xlsx";
//        String tablePath = "config\\excel";
        String tableType = "json";
        String tablePath = "config\\json";


        ConfigLoader configLoader = new ConfigLoader(definitionPaths, tablePath);
        configLoader.setPackagePrefix("quan.config");
        configLoader.setEnumPackagePrefix("quan");
        configLoader.setLoadType(ConfigLoader.Type.validateAndLoad);
        configLoader.setValidatorsPackage("quan");
        configLoader.setTableType(tableType);


        loadConfigs(configLoader);

        writeJson(configLoader);

        reloadAllConfigs(configLoader);

        reloadConfigs(configLoader);

        reloadTables(configLoader);

    }

    private static void loadConfigs(ConfigLoader configLoader) throws Exception {
        System.err.println("configLoader.loadConfigs()=============");
        long startTime = System.currentTimeMillis();
        try {
            configLoader.load();
        } catch (ConfigException e) {
            printErrors(e);
        }
        printConfigs();
        System.err.println("configLoader.loadConfigs()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void writeJson(ConfigLoader configLoader) {
        if (configLoader.getTableType().equals("json")) {
            return;
        }
        System.err.println("configLoader.writeJson()=============");
        long startTime = System.currentTimeMillis();
        configLoader.writeJson("config\\json");
        System.err.println("configLoader.writeJson()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void reloadAllConfigs(ConfigLoader configLoader) throws Exception {
        Thread.sleep(10000);
        System.err.println("configLoader.reloadAllConfigs()=============");
        long startTime = System.currentTimeMillis();
        try {
            configLoader.reloadAll();
        } catch (ConfigException e) {
            printErrors(e);
        }
        printConfigs();
        System.err.println("configLoader.reloadAllConfigs()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void reloadConfigs(ConfigLoader configLoader) throws Exception {
        Thread.sleep(5000);
        List<String> reloadConfigs = Arrays.asList("ItemConfig", "WeaponConfig");
        System.err.println("configLoader.reloadConfigs()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();
        try {
            configLoader.reload(reloadConfigs);
        } catch (ConfigException e) {
            printErrors(e);
        }
        printConfigs();
        System.err.println("configLoader.reloadConfigs()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void reloadTables(ConfigLoader configLoader) throws Exception {
        Thread.sleep(5000);
        List<String> reloadConfigs = Arrays.asList("道具", "武器");
        System.err.println("configLoader.reloadTables()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();
        try {
            configLoader.reloadTables(reloadConfigs, true);
        } catch (ConfigException e) {
            printErrors(e);
        }
        printConfigs();
        System.err.println("configLoader.reloadTables()耗时:" + (System.currentTimeMillis() - startTime));
        System.err.println();
    }

    private static void printErrors(ConfigException e) {
        System.err.println("printErrors start============");
        for (String error : e.getErrors()) {
            System.err.println(error);
        }
        System.err.println("printErrors end============");
    }

    private static void printConfigs() throws Exception {
        System.err.println("printConfigs start============");
        Thread.sleep(100);
        System.err.println("ItemConfig============");
        ItemConfig.getIdConfigs().values().forEach(System.out::println);

        System.err.println("EquipConfig============");
        EquipConfig.self.getIdConfigs().values().forEach(System.out::println);

        System.err.println("WeaponConfig============");
        WeaponConfig.self.getIdConfigs().values().forEach(System.out::println);
        System.err.println("printConfigs end============");
    }
}
