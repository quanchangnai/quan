package quan.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import quan.generator.FieldDefinition;
import quan.generator.config.ConfigDefinition;

import java.io.FileInputStream;
import java.io.InputStreamReader;
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

        try {
            CSVParser parser = new CSVParser(new InputStreamReader(new FileInputStream(tableFile), "GBK"), CSVFormat.DEFAULT);
            records = parser.getRecords();
        } catch (Exception e) {
            String error = String.format("读取配置[%s]出错:%s", table, e.getMessage());
            errors.add(error);
            logger.debug(error, e);
            return;
        }

        //第一行是表头，第二行是注释，第三行起是内容
        if (records.size() <= 2) {
            return;
        }

        for (int i = 2; i < records.size(); i++) {
            CSVRecord record = records.get(i);
            JSONObject jsonObject = new JSONObject();

            for (int j = 0; j < record.size(); j++) {
                String columnName = records.get(0).get(j);
                String columnValue = record.get(j).trim();

                FieldDefinition fieldDefinition = configDefinition.getColumnFields().get(columnName);
                if (fieldDefinition == null) {
                    continue;
                }
                String fieldName = fieldDefinition.getName();
                String fieldType = fieldDefinition.getType();
                Object fieldValue;

                try {
                    fieldValue = convert(fieldDefinition, columnValue);
                } catch (Exception e) {
                    errors.add(String.format("配置[%s]的第%d行第%d列[%s]数据[%s]格式错误", table, i + 1, j + 1, columnName, columnValue));
                    continue;
                }

                if (fieldType.equals("list") || fieldType.equals("set")) {
                    JSONArray jsonArray = jsonObject.getJSONArray(fieldName);
                    if (jsonArray == null) {
                        jsonObject.put(fieldName, fieldValue);
                    } else {
                        jsonArray.addAll((JSONArray) fieldValue);
                    }
                } else {
                    jsonObject.put(fieldName, fieldValue);
                }
            }

            jsons.add(jsonObject);

        }
    }

}
