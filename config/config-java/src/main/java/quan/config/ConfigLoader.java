package quan.config;

import com.alibaba.fastjson.JSONObject;
import quan.generator.DefinitionParser;
import quan.generator.XmlDefinitionParser;
import quan.generator.config.ConfigDefinition;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 加载配置数据
 * Created by quanchangnai on 2019/7/30.
 */
public class ConfigLoader {

    protected DefinitionParser definitionParser = new XmlDefinitionParser();

    //配置的定义文件所在目录
    private List<String> definitionPaths;

    //配置表所在目录
    private String sourcePath;

    private String packagePrefix;

    private String enumPackagePrefix;

    public ConfigLoader(List<String> definitionPaths, String sourcePath) {
        this.definitionPaths = definitionPaths;
        this.sourcePath = sourcePath;
    }

    public ConfigLoader setDefinitionParser(DefinitionParser definitionParser) {
        this.definitionParser = definitionParser;
        return this;
    }

    public ConfigLoader setPackagePrefix(String packagePrefix) {
        this.packagePrefix = packagePrefix;
        return this;
    }

    public ConfigLoader setEnumPackagePrefix(String enumPackagePrefix) {
        this.enumPackagePrefix = enumPackagePrefix;
        return this;
    }

    public void load() throws Exception {
        definitionParser.setDefinitionPaths(definitionPaths);
        definitionParser.setPackagePrefix(packagePrefix);
        definitionParser.setEnumPackagePrefix(enumPackagePrefix);

        definitionParser.parse();

        Map<String, ConfigDefinition> sourceConfigs = ConfigDefinition.getSourceConfigs();
        for (String source : sourceConfigs.keySet()) {
            ConfigDefinition configDefinition = sourceConfigs.get(source);
            File sourceFile = new File(sourcePath, source);
            ConfigSource configSource = new CSVConfigSource(sourceFile, configDefinition);
            try {
                List<JSONObject> objects = configSource.read();
                System.err.println(source);
                for (JSONObject object : objects) {
                    System.err.println(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
