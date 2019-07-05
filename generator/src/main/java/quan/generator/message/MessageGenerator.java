package quan.generator.message;

import freemarker.template.Template;
import quan.generator.BeanDefinition;
import quan.generator.Generator;

/**
 * Created by quanchangnai on 2017/7/6.
 */
public abstract class MessageGenerator extends Generator {

    public static void main(String[] args) throws Exception {
        String language = "java";

        String srcPath = "generator\\src\\test\\java\\quan\\generator\\message";
        String destPath = "generator\\src\\test\\java";
        String packagePrefix = "quan.generator.message";

//        String srcPath = "message-java\\src\\test\\java\\quan\\message";
//        String destPath = "message-java\\src\\test\\java";
//        String packagePrefix = "quan.message";

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

        MessageGenerator generator = null;
        switch (language) {
            case "java":
                generator = new JavaMessageGenerator(srcPath, destPath);
                break;
            default:
                break;
        }
        generator.setPackagePrefix(packagePrefix);
        generator.generate();
    }

    public static void usage() {
        System.err.println("-language 生成语言");
        System.err.println("-srcPath 描述文件的目录");
        System.err.println("-destPath 生成目标代码的目录");
    }


    public MessageGenerator(String srcPath, String destPath) throws Exception {
        super(srcPath, destPath);

        Template messageTemplate = freemarkerCfg.getTemplate("message." + getLanguage() + ".ftl");
        Template enumTemplate = freemarkerCfg.getTemplate("enum." + getLanguage() + ".ftl");

        templates.put(MessageDefinition.class, messageTemplate);
        templates.put(BeanDefinition.class, messageTemplate);
        templates.put(EnumDefinition.class, enumTemplate);
    }

}
