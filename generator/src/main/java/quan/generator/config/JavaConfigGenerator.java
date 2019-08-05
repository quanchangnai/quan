package quan.generator.config;

import quan.generator.Language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class JavaConfigGenerator extends ConfigGenerator {


    public JavaConfigGenerator(List<String> definitionPaths, String codePath) throws Exception {
        super(definitionPaths, codePath);

        basicTypes.put("date", "Date");
        basicTypes.put("time", "Date");
        basicTypes.put("datetime", "Date");
        classTypes.put("date", "Date");
        classTypes.put("time", "Date");
        classTypes.put("datetime", "Date");
    }

    public JavaConfigGenerator(String definitionPaths, String codePath) throws Exception {
        this(Collections.singletonList(definitionPaths), codePath);
    }

    @Override
    protected Language supportLanguage() {
        return Language.java;
    }

    public static void main(String[] args) throws Exception {

        List<String> definitionPaths = new ArrayList<>();
        definitionPaths.add("generator\\src\\test\\java\\quan\\generator");
        definitionPaths.add("generator\\src\\test\\java\\quan\\generator\\config");
        String destPath = "config\\config-java\\src\\test\\java";

        JavaConfigGenerator generator = new JavaConfigGenerator(definitionPaths, destPath);
        generator.setPackagePrefix("quan.config");
        generator.setEnumPackagePrefix("quan");

        generator.generate();
    }
}
