package quan.protocol.generator;

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
        String srcFile = "protocol\\src\\test\\java\\quan\\protocol\\protocols.xml";
        String destPath = "protocol\\src\\test\\java";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-language")) {
                language = args[++i];
            } else if (args[i].equals("-srcFile")) {
                srcFile = args[++i];
            } else if (args[i].equals("-destPath")) {
                destPath = args[++i];
            }
        }

        if (language == null || srcFile == null || destPath == null) {
            usage();
            System.exit(0);
        }

        Generator generator = null;
        switch (language) {
            case "java":
                generator = new JavaGenerator(srcFile, destPath);
                break;
            default:
                break;
        }
        generator.generate();
    }

    public static void usage() {
        System.err.println("-language 生成语言");
        System.err.println("-srcFile 协议描述xml文件");
        System.err.println("-destPath 生成代目标代码的目录");
    }

    protected String srcFile;
    protected String destPath;
    protected Configuration cfg;
    protected List<Definition> definitions;


    public Generator(String srcFile, String destPath) throws Exception {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassForTemplateLoading(JavaGenerator.class, "");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        this.srcFile = srcFile;
        this.destPath = destPath;
        this.cfg = cfg;
        this.definitions = new Parser(srcFile).parse();

    }

    protected abstract String getLanguage();

    protected void preprocess(List<Definition> definitions) {
    }

    protected void generate() throws Exception {
        Template protocolTemplate = cfg.getTemplate("protocol." + getLanguage() + ".ftl");
        Template enumTemplate = cfg.getTemplate("enum." + getLanguage() + ".ftl");

        preprocess(definitions);

        for (Definition definition : definitions) {
            Template template;
            String packageName;
            if (definition instanceof BeanDefinition) {
                BeanDefinition beanDefinition = (BeanDefinition) definition;
                packageName = beanDefinition.getPackageName();
                template = protocolTemplate;
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
