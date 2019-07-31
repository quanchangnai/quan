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
    protected void read0() {
        List<CSVRecord> records = new ArrayList<>();
        try {
            CSVParser parser = new CSVParser(new InputStreamReader(new FileInputStream(tableFile), "GBK"), CSVFormat.DEFAULT);
            records = parser.getRecords();
        } catch (Exception e) {
            logger.error("读取[{}]异常", table, e);
        }

        //第一行是表头，第二行是注释，第三行起是内容
        if (records.size() <= 2) {
            return;
        }

        for (int i = 2; i < records.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            CSVRecord record = records.get(i);
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
                    logger.error("表格[{}]的第{}行第{}列数据[{}]格式错误", table, i + 1, j + 1, columnValue);
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
