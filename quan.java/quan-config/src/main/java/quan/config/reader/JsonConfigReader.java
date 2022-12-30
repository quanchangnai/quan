package quan.config.reader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.Validate;
import quan.config.Config;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * JSON配置读取器
 */
@SuppressWarnings({"unchecked"})
public class JsonConfigReader extends ConfigReader {

    private final String configFullName;

    public JsonConfigReader(File jsonFile, String configFullName) {
        this.configFullName = configFullName;
        super.init(jsonFile, null);
    }

    @Override
    protected void initPrototype() {
        try {
            Class<Config> configClass = (Class<Config>) Class.forName(configFullName);
            prototype = configClass.getDeclaredConstructor(JSONObject.class).newInstance(new JSONObject());
        } catch (Exception e) {
            logger.error("实例化配置类[{}]失败", configFullName, e);
        }
    }

    @Override
    protected void read() {
        try (FileInputStream inputStream = new FileInputStream(tableFile)) {
            byte[] availableBytes = new byte[inputStream.available()];
            Validate.isTrue(inputStream.read(availableBytes) > 0);
            List<JSONObject> jsons = (List<JSONObject>) JSON.parse(availableBytes);
            this.jsons.addAll(jsons);
        } catch (Exception e) {
            logger.error("读取配置[{}]出错", tableFile.getName(), e);
        }
    }
}
