package quan.definition.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import quan.definition.Category;
import quan.definition.FieldDefinition;
import quan.definition.config.ConfigDefinition;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

/**
 * 在Excel表格中直接定义配置，不支持定义复杂结构
 */
public class CSVDefinitionParser extends DefinitionParser {

    {
        definitionFileEncoding = "GBK";
    }


    @Override
    protected String definitionFileType() {
        return "csv";
    }

    @Override
    protected void parseClasses(File definitionFile) {
        String name = definitionFile.getName().substring(0, definitionFile.getName().lastIndexOf("."));
        ConfigDefinition configDefinition = new ConfigDefinition(name, null);
        configDefinition.setParser(this);
        configDefinition.setDefinitionFile(definitionFile.getName());
        configDefinition.setName(name);
        parsedClasses.add(configDefinition);

        List<CSVRecord> records;
        try (CSVParser parser = new CSVParser(new InputStreamReader(new FileInputStream(definitionFile), definitionFileEncoding), CSVFormat.DEFAULT)) {
            records = parser.getRecords();
        } catch (Exception e) {
            logger.error("解析定义文件[{}]错误", definitionFile.getName(), e);
            return;
        }

        if (records.size() < 3) {
            return;
        }

        for (int i = 0; i < records.get(0).size(); i++) {
            String columnName = records.get(0).get(i);
            String fieldName = records.get(1).get(i);
            String fieldType = records.get(2).get(i);

            FieldDefinition fieldDefinition = new FieldDefinition();
            fieldDefinition.setParser(this);
            fieldDefinition.setCategory(getCategory());
            fieldDefinition.setName(fieldName);
            fieldDefinition.setTypes(fieldType);
            fieldDefinition.setColumn(columnName);

            configDefinition.addField(fieldDefinition);
        }

    }

}
