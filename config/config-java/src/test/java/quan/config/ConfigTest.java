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
        String sourcePath = "config\\config-java\\src\\test\\resources\\csv";

        ConfigLoader configLoader = new ConfigLoader(definitionPaths, sourcePath);
        configLoader.setPackagePrefix("quan.config");
        configLoader.setEnumPackagePrefix("quan");

        System.err.println("configLoader.load()=============");
        configLoader.load();

        printConfigs();

        Thread.sleep(10000);

        List<String> reloadTables = Arrays.asList("武器.csv");
        System.err.println("configLoader.reload()=============" + reloadTables);
        configLoader.reload(reloadTables);

        printConfigs();

    }


    private static void printConfigs() {
        System.err.println("ItemConfig============");
        ItemConfig.getIdConfigs().values().forEach(System.out::println);

        System.err.println("EquipConfig============");
        EquipConfig.self.getIdConfigs().values().forEach(System.out::println);

        System.err.println("WeaponConfig============");
        WeaponConfig.self.getIdConfigs().values().forEach(System.out::println);
    }
}
