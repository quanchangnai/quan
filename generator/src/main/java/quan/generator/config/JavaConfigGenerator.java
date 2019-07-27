package quan.generator.config;

import quan.config.Config;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.Language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class JavaConfigGenerator extends ConfigGenerator {


    public JavaConfigGenerator(List<String> srcPaths, String destPath) throws Exception {
        super(srcPaths, destPath);
    }

    public JavaConfigGenerator(String srcPath, String destPath) throws Exception {
        this(Collections.singletonList(srcPath), destPath);
    }

    @Override
    protected Language supportLanguage() {
        return Language.java;
    }


    @Override
    protected void processClassSelf(ClassDefinition classDefinition) {
        super.processClassSelf(classDefinition);
        if (!(classDefinition instanceof BeanDefinition)) {
            return;
        }

        BeanDefinition beanDefinition = (BeanDefinition) classDefinition;
        beanDefinition.getImports().add(Config.class.getPackage().getName() + ".*");
    }

    public static void main(String[] args) throws Exception {

        List<String> srcPaths = new ArrayList<>();
        srcPaths.add("generator\\src\\test\\java\\quan\\generator");
        srcPaths.add("generator\\src\\test\\java\\quan\\generator\\config");
        String destPath = "config\\config-java\\src\\test\\java";
        String packagePrefix = "quan.config";

        JavaConfigGenerator generator = new JavaConfigGenerator(srcPaths, destPath);

        generator.setPackagePrefix(packagePrefix);
        generator.setEnumPackagePrefix("quan");
        generator.generate();
    }
}
