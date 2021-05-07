package quan.editor.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.*;
import quan.config.TableType;
import quan.config.loader.DefinitionConfigLoader;
import quan.config.loader.LoadMode;
import quan.config.reader.ConfigReader;
import quan.definition.FieldDefinition;
import quan.definition.config.ConfigDefinition;
import quan.definition.parser.DefinitionParser;

/**
 * Created by quanchangnai on 2021/4/29.
 */
@RestController
@RequestMapping("/config")
public class ConfigController implements InitializingBean {

    private DefinitionConfigLoader configLoader;

    private DefinitionParser definitionParser;

    @Override
    public void afterPropertiesSet() throws Exception {
        String definitionPath = "quan-core\\definition\\config";
        String tablePath = "quan-core\\config\\excel";
        TableType tableType = TableType.xlsx;

        configLoader = new DefinitionConfigLoader(tablePath);
        definitionParser = configLoader.useXmlDefinition(definitionPath);
        configLoader.setTableType(tableType);
        configLoader.setLoadMode(LoadMode.ONLY_VALIDATE);

        try {
            configLoader.loadAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/tables")
    public Object tables() {
        List<Map<String, String>> tables = new ArrayList<>();

        for (String tableName : definitionParser.getTableConfigs().keySet()) {
            ConfigDefinition configDefinition = definitionParser.getTableConfigs().get(tableName);
            tables.add(Map.of("tableName", tableName, "configName", configDefinition.getLongName()));
        }

        return tables;
    }


    @PostMapping("/table")
    public Object table(@RequestParam String tableName) {
        ConfigDefinition configDefinition = definitionParser.getTableConfigs().get(tableName);
        ConfigReader configReader = configLoader.getReader(tableName);

        List<Object> fields = new ArrayList<>();
        for (FieldDefinition field : configDefinition.getFields()) {
            fields.add(Map.of("name", field.getName(),
                "column", field.getColumn(),
                "type", field.getType(),
                "showJson", field.isCollectionType() || field.isBeanType()));
        }

        return Map.of("fields", fields, "rows", configReader.getJsons());
    }

}
