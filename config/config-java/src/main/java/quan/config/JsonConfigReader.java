package quan.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import quan.generator.config.ConfigDefinition;

import java.io.FileInputStream;
import java.util.List;

/**
 * Created by quanchangnai on 2019/8/4.
 */
@SuppressWarnings({"unchecked"})
public class JsonConfigReader extends ConfigReader {

    public JsonConfigReader(String tablePath, String table, ConfigDefinition configDefinition) {
        super(tablePath, table, configDefinition);
    }

    @Override
    protected void read() {
        try (FileInputStream inputStream = new FileInputStream(tableFile)) {
            byte[] input = new byte[inputStream.available()];
            inputStream.read(input);

            List<JSONObject> jsons = (List<JSONObject>) JSON.parse(input);
            this.jsons.addAll(jsons);
        } catch (Exception e) {
            String error = String.format("读取配置[%s]出错:%s", table, e.getMessage());
            errors.add(error);
            logger.debug(error, e);
        }
    }
}
