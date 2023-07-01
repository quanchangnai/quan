package quan.data;

import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonReader;
import quan.data.bson.EntityCodecProvider;
import quan.data.bson.JsonStringWriter;

import java.util.Objects;

public interface Entity {

    @SuppressWarnings({"unchecked", "rawtypes"})
    default String toJson() {
        try (JsonStringWriter writer = new JsonStringWriter()) {
            Codec codec = EntityCodecProvider.DEFAULT_REGISTRY.get(getClass());
            codec.encode(writer, this, EncoderContext.builder().build());
            return writer.toJsonString();
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
