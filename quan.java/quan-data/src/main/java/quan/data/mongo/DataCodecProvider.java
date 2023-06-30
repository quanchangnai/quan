package quan.data.mongo;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.data.Bean;
import quan.data.Data;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unchecked")
public class DataCodecProvider implements CodecProvider {

    protected final static Logger logger = LoggerFactory.getLogger(DataCodecProvider.class);

    private static DataCodecProvider _default = new DataCodecProvider();

    private Map<Class<?>, Codec<?>> codecs = new HashMap<>();

    public DataCodecProvider() {
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (!Data.class.isAssignableFrom(clazz) && !Bean.class.isAssignableFrom(clazz)) {
            return null;
        }

        Codec<T> codec = (Codec<T>) codecs.get(clazz);

        if (codec == null) {
            try {
                Class<?> codecClass = Class.forName(clazz.getName() + "$CodecImpl");
                codec = (Codec<T>) codecClass.getDeclaredConstructor(CodecRegistry.class).newInstance(registry);
                codecs.put(codec.getEncoderClass(), codec);
            } catch (Exception e) {
                logger.error("", e);
            }
        }

        return codec;
    }

    public static DataCodecProvider getDefault() {
        return _default;
    }

}
