package quan.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import quan.definition.ConfigDefinition;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/14.
 */
public class CSVConfigReader extends ConfigReader {

    public CSVConfigReader(File tableFile, ConfigDefinition configDefinition) {
        super(tableFile, configDefinition);
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

        //第一行是表头
        List<String> columnNames = new ArrayList<>();
        for (String columnName : records.get(0)) {
            columnNames.add(columnName.trim());
        }
        validateColumnNames(columnNames);

        //第[bodyRowNum]行起是正文
        if (records.size() < bodyRowNum) {
            return;
        }

        for (int i = bodyRowNum; i <= records.size(); i++) {
            CSVRecord record = records.get(i - 1);
            JSONObject rowJson = new JSONObject(true);
            for (int j = 1; j <= columnNames.size(); j++) {
                addColumnToRow(rowJson, columnNames.get(j - 1), record.get(j - 1).trim(), i, j);
            }
            jsons.add(rowJson);
        }
    }

}
