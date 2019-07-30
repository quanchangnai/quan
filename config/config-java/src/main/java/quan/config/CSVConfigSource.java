package quan.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import quan.generator.FieldDefinition;
import quan.generator.config.ConfigDefinition;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/7/14.
 */
public class CSVConfigSource extends ConfigSource {

    public CSVConfigSource(File sourceFile, ConfigDefinition configDefinition) {
        super(sourceFile, configDefinition);
    }

    @Override
    public List<JSONObject> read() {
        List<JSONObject> list = new ArrayList<>();

        List<CSVRecord> records;
        try {
            CSVParser parser = new CSVParser(new InputStreamReader(new FileInputStream(sourceFile), "GBK"), CSVFormat.DEFAULT);
            records = parser.getRecords();
        } catch (Exception e) {
            logger.error("", e);
            return list;
        }
        if (records.isEmpty()) {
            return list;
        }

        for (int i = 1; i < records.size(); i++) {
            JSONObject object = new JSONObject();
            CSVRecord record = records.get(i);
            for (int j = 0; j < record.size(); j++) {
                String name = records.get(0).get(j);
                String value = record.get(j).trim();

                FieldDefinition fieldDefinition = configDefinition.getSourceFields().get(name);
                if (fieldDefinition == null) {
                    continue;
                }
                String fieldName = fieldDefinition.getName();
                String fieldType = fieldDefinition.getType();

                Object fieldValue = convert(fieldDefinition, value);

                if (fieldType.equals("list") || fieldType.equals("set")) {
                    JSONArray array = object.getJSONArray(fieldName);
                    if (array == null) {
                        array = new JSONArray();
                        object.put(fieldName, array);
                    }
                    array.addAll((JSONArray) fieldValue);
                } else {
                    object.put(fieldName, fieldValue);
                }
            }
            list.add(object);
        }

        return list;
    }

}
