package quan.data.bson;

import org.bson.json.JsonWriter;

import java.io.StringWriter;

public class JsonStringWriter extends JsonWriter {

    public JsonStringWriter() {
        super(new StringWriter());
    }

    public String toJsonString() {
        return getWriter().toString();
    }

}
