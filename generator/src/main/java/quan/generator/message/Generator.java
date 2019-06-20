package quan.generator.message;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class Generator {

    public static void main(String[] args) throws Exception {
        String language = "java";
        String srcPath = "generator\\src\\test\\java\\quan\\generator";
        String destPath = "generator\\src\\test\\java";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-language")) {
                language = args[++i];
            } else if (args[i].equals("-srcPath")) {
                srcPath = args[++i];
            } else if (args[i].equals("-destPath")) {
                destPath = args[++i];
            }
        }

        if (language == null || srcPath == null || destPath == null) {
            usage();
            System.exit(0);
        }

        Generator generator = null;
        switch (language) {
            case "java":
                generator = new JavaGenerator(srcPath, destPath);
                break;
            default:
                break;
        }
        generator.generate();
    }

    public static void usage() {
        System.err.println("-language 生成语言");
        System.err.println("-srcPath 消息描述xml的目录");
        System.err.println("-destPath 生成代目标代码的目录");
    }

    protected String srcPath;
    protected String destPath;
    protected Configuration freemarkerCfg;
    protected List<Definition> definitions;


    public Generator(String srcPath, String destPath) throws Exception {

        Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);
        freemarkerCfg.setClassForTemplateLoading(JavaGenerator.class, "");
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        this.srcPath = srcPath;
        this.destPath = destPath;
        this.freemarkerCfg = freemarkerCfg;
        this.definitions = new Parser(srcPath).parse();

    }

    protected abstract String getLanguage();

    protected void preprocess(List<Definition> definitions) {
    }

    protected void generate() throws Exception {
        Template messageTemplate = freemarkerCfg.getTemplate("message." + getLanguage() + ".ftl");
        Template enumTemplate = freemarkerCfg.getTemplate("enum." + getLanguage() + ".ftl");

        preprocess(definitions);

        for (Definition definition : definitions) {
            Template template;
            String packageName;
            if (definition instanceof BeanDefinition) {
                BeanDefinition beanDefinition = (BeanDefinition) definition;
                packageName = beanDefinition.getPackageName();
                template = messageTemplate;
            } else {
                EnumDefinition enumDefinition = (EnumDefinition) definition;
                packageName = enumDefinition.getPackageName();
                template = enumTemplate;
            }

            String packagePath = packageName.replace(".", "\\");
            File destFilePath = new File(destPath + "\\" + packagePath);
            if (!destFilePath.exists()) {
                destFilePath.mkdir();
            }
            Writer file = new FileWriter(new File(destFilePath, definition.getName() + "." + getLanguage()));
            template.process(definition, file);
        }
    }


}
