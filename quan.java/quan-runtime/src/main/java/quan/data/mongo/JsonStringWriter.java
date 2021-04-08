package quan.data.mongo;

import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

import java.io.Writer;

public class JsonStringWriter extends JsonWriter {

    @SuppressWarnings("deprecation")
    public JsonStringWriter(Writer writer) {
        super(writer, new JsonWriterSettings(JsonMode.RELAXED));
    }

    public JsonStringWriter(Writer writer, JsonWriterSettings settings) {
        super(writer, settings);
    }

}
