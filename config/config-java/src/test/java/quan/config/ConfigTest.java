package quan.config;

import java.util.*;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class ConfigTest {


    public static void main(String[] args) throws Exception {
            List<String> definitionPaths = new ArrayList<>();
            definitionPaths.add("generator\\src\\test\\java\\quan\\generator");
            definitionPaths.add("generator\\src\\test\\java\\quan\\generator\\config");
            String sourcePath = "config\\config-java\\src\\test\\resources";

            ConfigLoader configLoader = new ConfigLoader(definitionPaths, sourcePath);
            configLoader.setPackagePrefix("quan.config");
            configLoader.setEnumPackagePrefix("quan");

            configLoader.load();
//
//            System.err.println(ConfigDefinition.getSourceConfigs());

//            CSVParser parser = new CSVParser(new InputStreamReader(new FileInputStream("E:\\道具.csv"), "GBK"), CSVFormat.DEFAULT);
//
//            System.err.println(parser.getRecords());



    }

}
