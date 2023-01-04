package quan.definition.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import quan.definition.config.ConfigDefinition;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

/**
 * 基于CSV表格的【定义】解析器
 *
 * @see TableDefinitionParser
 */
public class CSVDefinitionParser extends TableDefinitionParser {

    {
        definitionFileEncoding = "GBK";
    }

    @Override
    public String getDefinitionType() {
        return "csv";
    }

    @Override
    protected boolean parseTable(ConfigDefinition configDefinition, File definitionFile) {
        List<CSVRecord> records;
        try (CSVParser parser = new CSVParser(new InputStreamReader(Files.newInputStream(definitionFile.toPath()), definitionFileEncoding), CSVFormat.DEFAULT)) {
            records = parser.getRecords();
        } catch (Exception e) {
            logger.error("解析定义文件[{}]错误", definitionFile.getName(), e);
            return false;
        }

        if (records.size() < 3) {
            addValidatedError(configDefinition.getValidatedName() + "的定义文件不完整，表头要求第1行列名、第2行字段名、第3行字段约束");
            return false;
        }

        for (int i = 0; i < records.get(0).size(); i++) {
            String columnName = records.get(0).get(i);
            String fieldName = records.get(1).get(i);
            String fieldConstraint = records.get(2).get(i);
            addField(configDefinition, columnName, fieldName, fieldConstraint);
        }

        return true;
    }
}
