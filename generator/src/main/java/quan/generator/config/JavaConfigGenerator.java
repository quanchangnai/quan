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

}
