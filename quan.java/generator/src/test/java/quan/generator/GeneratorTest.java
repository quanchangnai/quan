package quan.generator;

import quan.config.TableType;
import quan.definition.DefinitionParser;
import quan.definition.XmlDefinitionParser;
import quan.generator.config.CSharpConfigGenerator;
import quan.generator.config.JavaConfigGenerator;
import quan.generator.database.DatabaseGenerator;
import quan.generator.message.CSharpMessageGenerator;
import quan.generator.message.JavaMessageGenerator;
import quan.generator.message.LuaMessageGenerator;

/**
 * Created by quanchangnai on 2019/8/29.
 */
public class GeneratorTest {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        generateData();
        generateMessage();
        generateConfig();
        System.err.println("GeneratorTest耗时(ms):" + (System.currentTimeMillis() - start));
    }

    private static void generateData() {
        System.err.println("DatabaseGenerator.generate()==========================");
        DatabaseGenerator databaseGenerator = new DatabaseGenerator("database\\src\\test\\java");
        databaseGenerator.useXmlDefinitionParser("definition\\database", "quan.database");
        databaseGenerator.generate();
        System.err.println();
    }

    private static void generateMessage() {
        DefinitionParser definitionParser = new XmlDefinitionParser();
        definitionParser.setDefinitionPath("definition\\message");

        System.err.println("JavaMessageGenerator.generate()==========================");
        JavaMessageGenerator javaMessageGenerator = new JavaMessageGenerator("message\\src\\test\\java");
        definitionParser.setPackagePrefix("quan.message");
        javaMessageGenerator.setDefinitionParser(definitionParser);
        javaMessageGenerator.generate();
        System.err.println();

        System.err.println("CSharpMessageGenerator.generate()==========================");
        CSharpMessageGenerator cSharpMessageGenerator = new CSharpMessageGenerator("..\\quan.cs");
        definitionParser.setPackagePrefix("Test.Message");
        cSharpMessageGenerator.setDefinitionParser(definitionParser);
        cSharpMessageGenerator.generate();
        System.err.println();

        System.err.println("LuaMessageGenerator.generate()==========================");
        LuaMessageGenerator luaMessageGenerator = new LuaMessageGenerator("..\\quan.lua\\test\\src");
        definitionParser.setPackagePrefix("test.message");
        luaMessageGenerator.setDefinitionParser(definitionParser);
        luaMessageGenerator.generate();
        System.err.println();
    }

    private static void generateConfig() {
        DefinitionParser definitionParser = new XmlDefinitionParser();
        definitionParser.setDefinitionPath("definition\\config");

        System.err.println("JavaConfigGenerator.generate()==========================");
        JavaConfigGenerator javaConfigGenerator = new JavaConfigGenerator("config\\src\\test\\java");
        definitionParser.setPackagePrefix("quan.config");
        javaConfigGenerator.setDefinitionParser(definitionParser);
        javaConfigGenerator.initConfigLoader(TableType.xlsx, "config\\excel");
        javaConfigGenerator.generate();
        System.err.println();

        System.err.println("CSharpConfigGenerator.generate()==========================");
        CSharpConfigGenerator cSharpConfigGenerator = new CSharpConfigGenerator("..\\quan.cs");
        definitionParser.setPackagePrefix("Test.Config");
        cSharpConfigGenerator.setDefinitionParser(definitionParser);
        cSharpConfigGenerator.initConfigLoader(TableType.xlsx, "config\\excel");
        cSharpConfigGenerator.generate();
        System.err.println();
    }
}
