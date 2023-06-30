package quan.data.mongo;

import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;
import quan.data.Bean;
import quan.data.Data;

import java.io.StringWriter;

@SuppressWarnings({"unchecked", "rawtypes"})
public class DataJsonWriter extends JsonWriter {

    private static final CodecRegistry codecRegistry = CodecRegistries.fromProviders(DataCodecProvider.getDefault());

    public DataJsonWriter(Bean bean) {
        super(new StringWriter(), JsonWriterSettings.builder().build());
        Codec codec = codecRegistry.get(bean.getClass());
        codec.encode(this, bean, EncoderContext.builder().build());
    }

    public DataJsonWriter(Data<?> data) {
        super(new StringWriter(), JsonWriterSettings.builder().build());
        Codec codec = codecRegistry.get(data.getClass());
        codec.encode(this, data, EncoderContext.builder().build());
    }

    public String toJson() {
        return getWriter().toString();
    }

}
