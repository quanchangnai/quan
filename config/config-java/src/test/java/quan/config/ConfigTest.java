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
        String tableType = "xlsx";
        String tablePath = "config\\excel";


        ConfigLoader configLoader = new ConfigLoader(definitionPaths, tablePath);
        configLoader.setPackagePrefix("quan.config");
        configLoader.setEnumPackagePrefix("quan");
//        configLoader.onlyCheck(true);
        configLoader.setValidatorsPackage("quan");
        configLoader.setTableType(tableType);

        System.err.println("configLoader.load()=============");
        long startTime = System.currentTimeMillis();
        try {
            configLoader.load();
        } catch (ConfigException e) {
            printErrors(e);
        }
        System.err.println("configLoader.load()耗时:" + (System.currentTimeMillis() - startTime));
        printConfigs();

        Thread.sleep(10000);

        reloadConfigs(configLoader);

        Thread.sleep(10000);

        reloadTables(configLoader);

    }

    private static void reloadConfigs(ConfigLoader configLoader) throws Exception {
        List<String> reloadConfigs = Arrays.asList("ItemConfig", "WeaponConfig");
        System.err.println("configLoader.reloadConfigs()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();
        try {
            configLoader.reloadConfigs(reloadConfigs);
        } catch (ConfigException e) {
            printErrors(e);
        }
        System.err.println("configLoader.reloadConfigs()耗时:" + (System.currentTimeMillis() - startTime));
        printConfigs();
    }

    private static void reloadTables(ConfigLoader configLoader) throws Exception {
        List<String> reloadConfigs = Arrays.asList("道具", "武器");
        System.err.println("configLoader.reloadTables()=============" + reloadConfigs);
        long startTime = System.currentTimeMillis();
        try {
            configLoader.reloadTables(reloadConfigs);
        } catch (ConfigException e) {
            printErrors(e);
        }
        System.err.println("configLoader.reloadTables()耗时:" + (System.currentTimeMillis() - startTime));
        printConfigs();
    }

    private static void printErrors(ConfigException e) {
        System.err.println("printErrors============");
        for (String error : e.getErrors()) {
            System.err.println(error);
        }
        System.err.println("printErrors============");
    }

    private static void printConfigs() throws Exception {
        Thread.sleep(100);
        System.err.println("ItemConfig============");
        ItemConfig.getIdConfigs().values().forEach(System.out::println);

        System.err.println("EquipConfig============");
        EquipConfig.self.getIdConfigs().values().forEach(System.out::println);

        System.err.println("WeaponConfig============");
        WeaponConfig.self.getIdConfigs().values().forEach(System.out::println);
    }
}
