package quan.data;

import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;

import java.io.StringWriter;
import java.util.Objects;

public interface Entity {

    @SuppressWarnings({"unchecked", "rawtypes"})
    default String toJson() {
        try (StringWriter stringWriter = new StringWriter()) {
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            Codec codec = EntityCodecProvider.DEFAULT_REGISTRY.get(getClass());
            codec.encode(jsonWriter, this, EncoderContext.builder().build());
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <T extends Entity> T parseJson(Class<T> clazz, String json) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(json);
        try (JsonReader reader = new JsonReader(json)) {
            Codec<T> codec = EntityCodecProvider.DEFAULT_REGISTRY.get(clazz);
            return codec.decode(reader, DecoderContext.builder().build());
        }
    }

}
