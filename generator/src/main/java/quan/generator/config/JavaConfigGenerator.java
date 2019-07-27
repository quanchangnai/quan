package quan.generator.config;

import quan.config.Config;
import quan.generator.BeanDefinition;
import quan.generator.ClassDefinition;
import quan.generator.Language;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class JavaConfigGenerator extends ConfigGenerator {


    public JavaConfigGenerator(String srcPath, String destPath) throws Exception {
        super(srcPath, destPath);
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

        String srcPath = "generator\\src\\test\\java\\quan\\generator\\config";
        String destPath = "config\\config-java\\src\\test\\java";
        String packagePrefix = "quan.config";

        JavaConfigGenerator generator = new JavaConfigGenerator(srcPath, destPath);

        generator.setPackagePrefix(packagePrefix);
        generator.generate();
    }
}
