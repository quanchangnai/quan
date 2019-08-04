package quan.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import quan.generator.config.ConfigDefinition;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/14.
 */
public class CSVConfigReader extends ConfigReader {

    public CSVConfigReader(String tablePath, String table, ConfigDefinition configDefinition) {
        super(tablePath, table, configDefinition);
    }

    @Override
    protected void read() {
        clear();

        List<CSVRecord> records;
        try (CSVParser parser = new CSVParser(new InputStreamReader(new FileInputStream(tableFile), "GBK"), CSVFormat.DEFAULT)) {
            records = parser.getRecords();
        } catch (Exception e) {
            logger.error("读取配置[{}]出错", tableFile.getName(), e);
            return;
        }

        if (records.size() < 1) {
            return;
        }

        //第一行是表头，第二行是注释，第三行起是内容
        List<String> columnNames = new ArrayList<>();
        for (String columnName : records.get(0)) {
            columnNames.add(columnName.trim());
        }
        validateColumnNames(columnNames);

        if (records.size() <= 2) {
            return;
        }

        for (int i = 2; i < records.size(); i++) {
            CSVRecord record = records.get(i);
            JSONObject rowJson = new JSONObject(true);
            for (int j = 0; j < columnNames.size(); j++) {
                addColumnToRow(rowJson, columnNames.get(j), record.get(j).trim(), i + 1, j + 1);
            }
            jsons.add(rowJson);
        }
    }

}
