package quan.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.FileInputStream;
import java.util.List;

/**
 * Created by quanchangnai on 2019/8/4.
 */
@SuppressWarnings({"unchecked"})
public class JsonConfigReader extends ConfigReader {

    private String configFullName;

    public JsonConfigReader(String tablePath, String table, String configFullName) {
        this.configFullName = configFullName;
        super.init(tablePath, table, null);
    }

    @Override
    protected void initPrototype() {
        if (configDefinition != null) {
            super.initPrototype();
            return;
        }

        try {
            Class<Config> configClass = (Class<Config>) Class.forName(configFullName);
            prototype = configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.error("实例化配置类[{}]失败", configFullName, e);
        }
    }

    @Override
    protected void read() {
        try (FileInputStream inputStream = new FileInputStream(tableFile)) {
            byte[] input = new byte[inputStream.available()];
            inputStream.read(input);

            List<JSONObject> jsons = (List<JSONObject>) JSON.parse(input);
            this.jsons.addAll(jsons);
        } catch (Exception e) {
            logger.error("读取配置[{}]出错", tableFile.getName(), e);
        }
    }
}
