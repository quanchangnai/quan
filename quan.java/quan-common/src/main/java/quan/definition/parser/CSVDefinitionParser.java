package quan.definition.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import quan.definition.config.ConfigDefinition;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Collection;
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

    public CSVDefinitionParser() {
    }

    public CSVDefinitionParser(Collection<String> definitionPaths) {
        setDefinitionPaths(definitionPaths);
    }

    @Override
    protected boolean parseTable(ConfigDefinition configDefinition, File definitionFile) {
        List<CSVRecord> records;
        try (CSVParser parser = new CSVParser(new InputStreamReader(Files.newInputStream(definitionFile.toPath()), definitionFileEncoding), CSVFormat.DEFAULT)) {
            records = parser.getRecords();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (records.size() < 3) {
            addValidatedError(configDefinition.getValidatedName() + "的定义文件不完整，要求表头第1行是字段名、第2行是字段约束、第3行是校验表达式、第4行是字段注释");
            return false;
        }

        for (int i = 0; i < records.get(0).size(); i++) {
            String fieldName = records.get(0).get(i);
            String constraints = records.get(1).get(i);
            String validation = records.get(2).get(i);
            String comment = records.get(3).get(i);
            addField(configDefinition, fieldName, constraints, validation, comment);
        }

        return true;
    }
}
