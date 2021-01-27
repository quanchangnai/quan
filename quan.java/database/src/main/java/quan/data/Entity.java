package quan.data;

import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonWriter;
import quan.data.mongo.CodecsRegistry;
import quan.data.mongo.JsonStringWriter;

import java.io.StringWriter;

/**
 * 数据实体
 * Created by quanchangnai on 2019/5/16.
 */
public abstract class Entity extends Node {

    @SuppressWarnings({"unchecked"})
    public String toJson() {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonStringWriter(stringWriter);
        Codec codec = CodecsRegistry.getDefault().get(getClass());
        codec.encode(jsonWriter, this, EncoderContext.builder().build());
        return stringWriter.toString();
    }

}
