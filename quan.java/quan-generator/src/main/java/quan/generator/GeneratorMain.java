package quan.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.definition.parser.DefinitionParser;
import quan.definition.parser.XmlDefinitionParser;
import quan.generator.config.CSharpConfigGenerator;
import quan.generator.config.JavaConfigGenerator;
import quan.generator.config.LuaConfigGenerator;
import quan.generator.data.DataGenerator;
import quan.generator.message.CSharpMessageGenerator;
import quan.generator.message.JavaMessageGenerator;
import quan.generator.message.LuaMessageGenerator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static quan.definition.parser.DefinitionParser.createParser;

/**
 * 生成器启动入口<br/>
 * Created by quanchangnai on 2021/4/16.
 */
public class GeneratorMain {

    protected static final Logger logger = LoggerFactory.getLogger(GeneratorMain.class);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Properties options = loadOptions(args);
        if (options == null) {
            return;
        }

        DataGenerator dataGenerator = new DataGenerator(options);
        dataGenerator.useXmlParser();

        JavaMessageGenerator javaMessageGenerator = new JavaMessageGenerator(options);
        CSharpMessageGenerator cSharpMessageGenerator = new CSharpMessageGenerator(options);
        LuaMessageGenerator luaMessageGenerator = new LuaMessageGenerator(options);

        DefinitionParser messageParser = new XmlDefinitionParser();
        javaMessageGenerator.setParser(messageParser);
        cSharpMessageGenerator.setParser(messageParser);
        luaMessageGenerator.setParser(messageParser);

        DefinitionParser configParser = createParser(options.getProperty("config.definitionType").trim());
        JavaConfigGenerator javaConfigGenerator = new JavaConfigGenerator(options);
        CSharpConfigGenerator cSharpConfigGenerator = new CSharpConfigGenerator(options);
        LuaConfigGenerator luaConfigGenerator = new LuaConfigGenerator(options);

        javaConfigGenerator.setParser(configParser);
        cSharpConfigGenerator.setParser(configParser);
        luaConfigGenerator.setParser(configParser);

        dataGenerator.generate(true);

        javaMessageGenerator.generate(false);
        cSharpMessageGenerator.generate(false);
        luaMessageGenerator.generate(false);
        javaMessageGenerator.printErrors();

        javaConfigGenerator.generate(false);
        cSharpConfigGenerator.generate(false);
        luaConfigGenerator.generate(false);
        javaConfigGenerator.printErrors();

        logger.info("生成完成，耗时{}s", (System.currentTimeMillis() - startTime) / 1000D);
    }

    private static Properties loadOptions(String[] args) {
        String optionsFileName = "generator.properties";

        if (args.length > 0) {
            optionsFileName = args[0];
        } else {
            logger.info("使用默认位置的生成器选项配置文件[{}]\n", optionsFileName);
        }

        try (InputStream inputStream = new FileInputStream(optionsFileName)) {
            Properties options = new Properties();
            options.load(inputStream);
            return options;
        } catch (IOException e) {
            logger.info("加载生成器选项配置文件[{}]出错", optionsFileName, e);
            return null;
        }
    }

}
