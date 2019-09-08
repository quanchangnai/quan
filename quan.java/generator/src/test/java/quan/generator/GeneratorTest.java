package quan.generator;

import quan.config.TableType;
import quan.generator.config.CSharpConfigGenerator;
import quan.generator.config.JavaConfigGenerator;
import quan.generator.database.DatabaseGenerator;
import quan.generator.message.CSharpMessageGenerator;
import quan.generator.message.JavaMessageGenerator;
import quan.generator.message.LuaMessageGenerator;

import java.util.ArrayList;
import java.util.List;

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
        String definitionPath = "definition\\message";

        System.err.println("JavaMessageGenerator.generate()==========================");
        JavaMessageGenerator javaMessageGenerator = new JavaMessageGenerator("message\\src\\test\\java");
        javaMessageGenerator.useXmlDefinitionParser(definitionPath, "quan.message");
        javaMessageGenerator.generate();
        System.err.println();

        System.err.println("CSharpMessageGenerator.generate()==========================");
        CSharpMessageGenerator cSharpMessageGenerator = new CSharpMessageGenerator("..\\quan.cs");
        cSharpMessageGenerator.useXmlDefinitionParser(definitionPath, "Test.Message");
        cSharpMessageGenerator.generate();
        System.err.println();

        System.err.println("LuaMessageGenerator.generate()==========================");
        LuaMessageGenerator luaMessageGenerator = new LuaMessageGenerator("..\\quan.lua\\test\\src");
        luaMessageGenerator.useXmlDefinitionParser(definitionPath, "test.message");
        luaMessageGenerator.generate();
        System.err.println();
    }

    private static void generateConfig() {
        List<String> definitionPaths = new ArrayList<>();
        definitionPaths.add("definition\\config");

        System.err.println("JavaConfigGenerator.generate()==========================");
        JavaConfigGenerator javaConfigGenerator = new JavaConfigGenerator("config\\src\\test\\java");
        javaConfigGenerator.useXmlDefinitionParser(definitionPaths, "quan.config");
        javaConfigGenerator.initConfigLoader("config\\excel", TableType.xlsx);
        javaConfigGenerator.generate();
        System.err.println();

        System.err.println("CSharpConfigGenerator.generate()==========================");
        CSharpConfigGenerator cSharpConfigGenerator = new CSharpConfigGenerator("..\\quan.cs");
        cSharpConfigGenerator.useXmlDefinitionParser(definitionPaths, "Test.Config");
        cSharpConfigGenerator.initConfigLoader("config\\excel", TableType.xlsx);
        cSharpConfigGenerator.generate();
        System.err.println();
    }
}
