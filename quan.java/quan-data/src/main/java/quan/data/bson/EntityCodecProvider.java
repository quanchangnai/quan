package quan.data.bson;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.data.Entity;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unchecked")
public class EntityCodecProvider implements CodecProvider {

    protected final static Logger logger = LoggerFactory.getLogger(EntityCodecProvider.class);

    public static final EntityCodecProvider DEFAULT_PROVIDER = new EntityCodecProvider();

    public static final CodecRegistry DEFAULT_REGISTRY = CodecRegistries.fromProviders(DEFAULT_PROVIDER);

    private Map<Class<?>, Codec<?>> codecs = new HashMap<>();

    public EntityCodecProvider() {
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (!Entity.class.isAssignableFrom(clazz)) {
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

}
