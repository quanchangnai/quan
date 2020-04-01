package quan.database;

import com.mongodb.MongoClientSettings;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.common.ClassUtils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by quanchangnai on 2020/4/1.
 */
public class DataCodecRegistry implements CodecRegistry {

    protected final static Logger logger = LoggerFactory.getLogger(DataCodecRegistry.class);

    private Map<Class<?>, EntityCodec<?>> codecs = new HashMap<>();

    private CodecRegistry registry = CodecRegistries.fromRegistries(new CodecRegistry() {
        @Override
        public <T> Codec<T> get(Class<T> clazz) {
            return (Codec<T>) codecs.get(clazz);
        }
    }, MongoClientSettings.getDefaultCodecRegistry());

    public DataCodecRegistry() {
    }

    public void register(String packageName) {
        Set<Class<?>> codecClasses = ClassUtils.loadClasses(packageName, EntityCodec.class);
        for (Class<?> codecClass : codecClasses) {
            if (Modifier.isAbstract(codecClass.getModifiers())) {
                continue;
            }
            try {
                EntityCodec codec = (EntityCodec) codecClass.getDeclaredConstructor(CodecRegistry.class).newInstance(this);
                codecs.put(codec.getEncoderClass(), codec);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz) {
        return registry.get(clazz);
    }
}
