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
        definitionPaths.add("generator\\src\\test\\java\\quan\\generator\\config");
        String tablePath = "config\\config-java\\src\\test\\resources\\csv";

        ConfigLoader configLoader = new ConfigLoader(definitionPaths, tablePath);
        configLoader.setPackagePrefix("quan.config");
        configLoader.setEnumPackagePrefix("quan");
//        configLoader.onlyCheck(true);
        configLoader.setCheckerPackage("quan");

        System.err.println("configLoader.load()=============");
        try {
            configLoader.load();
        } catch (ConfigException e) {
            printErrors(e);
        }
        printConfigs();

        Thread.sleep(10000);

        List<String> reloadTables = Arrays.asList("道具.csv", "武器.csv");
        System.err.println("configLoader.reload()=============" + reloadTables);
        try {
            configLoader.reload(reloadTables);
        } catch (ConfigException e) {
            printErrors(e);
        }
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
