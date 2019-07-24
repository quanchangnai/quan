package quan.generator.config;

/**
 * Created by quanchangnai on 2019/7/11.
 */
public class JavaConfigGenerator extends ConfigGenerator {


    public JavaConfigGenerator(String srcPath, String destPath) throws Exception {
        super(srcPath, destPath);
    }

    @Override
    protected String getLanguage() {
        return "java";
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
