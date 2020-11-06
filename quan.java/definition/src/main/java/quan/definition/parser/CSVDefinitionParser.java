package quan.definition.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import quan.definition.config.ConfigDefinition;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 基于CSV表格的定义文件解析器，在表格中直接定义配置，不支持定义复杂结构
 */
public class CSVDefinitionParser extends TableDefinitionParser {

    {
        definitionFileEncoding = "GBK";
    }


    @Override
    protected String definitionFileType() {
        return "csv";
    }

    @Override
    protected boolean parseTable(ConfigDefinition configDefinition, File definitionFile) {
        List<CSVRecord> records;
        try (CSVParser parser = new CSVParser(new InputStreamReader(new FileInputStream(definitionFile), definitionFileEncoding), CSVFormat.DEFAULT)) {
            records = parser.getRecords();
        } catch (Exception e) {
            logger.error("解析定义文件[{}]错误", definitionFile.getName(), e);
            return false;
        }

        if (records.size() < 3) {
            addValidatedError(configDefinition.getValidatedName() + "的定义文件不完整");
            return false;
        }

        for (int i = 0; i < records.get(0).size(); i++) {
            String columnName = records.get(0).get(i);
            String fieldName = records.get(1).get(i);
            String constraint = records.get(2).get(i);

            addField(configDefinition, columnName, fieldName, constraint);
        }

        return true;
    }
}
