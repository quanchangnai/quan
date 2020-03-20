package quan.generator;

import quan.config.TableType;
import quan.definition.parser.DefinitionParser;
import quan.definition.parser.XmlDefinitionParser;
import quan.generator.config.CSharpConfigGenerator;
import quan.generator.config.JavaConfigGenerator;
import quan.generator.config.LuaConfigGenerator;
import quan.generator.database.DatabaseGenerator;
import quan.generator.message.CSharpMessageGenerator;
import quan.generator.message.JavaMessageGenerator;
import quan.generator.message.LuaMessageGenerator;

/**
 * Created by quanchangnai on 2019/8/29.
 */
public class GeneratorTest {

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        generateData();
        generateMessage();
        generateConfig();

        System.err.println("GeneratorTest耗时(ms):" + (System.currentTimeMillis() - start));
    }

    private static void generateData() {
        System.err.println("DatabaseGenerator.generate()==========================");
        DatabaseGenerator databaseGenerator = new DatabaseGenerator();
        databaseGenerator.setCodePath("database\\src\\test\\java");
        databaseGenerator.useXmlDefinitionParser("definition\\database");
        databaseGenerator.setPackagePrefix("quan.database");
        databaseGenerator.generate();
        System.err.println();
    }

    private static void generateMessage() {
        DefinitionParser definitionParser = new XmlDefinitionParser();
        definitionParser.setDefinitionPath("definition\\message");

        System.err.println("JavaMessageGenerator.generate()==========================");
        JavaMessageGenerator javaMessageGenerator = new JavaMessageGenerator();
        javaMessageGenerator.setCodePath("message\\src\\test\\java");
        definitionParser.setPackagePrefix("quan.message");
        javaMessageGenerator.setDefinitionParser(definitionParser);
        javaMessageGenerator.generate();
        System.err.println();

        System.err.println("CSharpMessageGenerator.generate()==========================");
        CSharpMessageGenerator cSharpMessageGenerator = new CSharpMessageGenerator();
        cSharpMessageGenerator.setCodePath("..\\quan.cs");
        definitionParser.setPackagePrefix("Test.Message");
        cSharpMessageGenerator.setDefinitionParser(definitionParser);
        cSharpMessageGenerator.generate();
        System.err.println();

        System.err.println("LuaMessageGenerator.generate()==========================");
        LuaMessageGenerator luaMessageGenerator = new LuaMessageGenerator();
        luaMessageGenerator.setCodePath("..\\quan.lua\\src");
        definitionParser.setPackagePrefix("test.message");
        luaMessageGenerator.setDefinitionParser(definitionParser);
        luaMessageGenerator.generate();
        System.err.println();
    }

    private static void generateConfig() {
        DefinitionParser definitionParser = new XmlDefinitionParser();
        definitionParser.setDefinitionPath("definition\\config");

        System.err.println("JavaConfigGenerator.generate()==========================");
        JavaConfigGenerator javaConfigGenerator = new JavaConfigGenerator();
        javaConfigGenerator.setCodePath("config\\src\\test\\java");
        definitionParser.setPackagePrefix("quan.config");
        javaConfigGenerator.setDefinitionParser(definitionParser);
        javaConfigGenerator.setTableType(TableType.xlsx);
        javaConfigGenerator.setTablePath("config\\excel");
        javaConfigGenerator.generate();
        System.err.println();

        System.err.println("CSharpConfigGenerator.generate()==========================");
        CSharpConfigGenerator cSharpConfigGenerator = new CSharpConfigGenerator();
        cSharpConfigGenerator.setCodePath("..\\quan.cs");
        definitionParser.setPackagePrefix("Test.Config");
        cSharpConfigGenerator.setDefinitionParser(definitionParser);
        cSharpConfigGenerator.setTableType(TableType.xlsx);
        cSharpConfigGenerator.setTablePath("config\\excel");
        cSharpConfigGenerator.generate();
        System.err.println();

        System.err.println("LuaConfigGenerator.generate()==========================");
        LuaConfigGenerator luaConfigGenerator = new LuaConfigGenerator();
        luaConfigGenerator.setCodePath("..\\quan.lua\\src");
        definitionParser.setPackagePrefix("test.config");
        luaConfigGenerator.setDefinitionParser(definitionParser);
        luaConfigGenerator.setTableType(TableType.xlsx);
        luaConfigGenerator.setTablePath("config\\excel");
        luaConfigGenerator.generate();
        System.err.println();
    }
}
