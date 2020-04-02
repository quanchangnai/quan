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

    private Map<Class<?>, Codec<?>> codecs = new HashMap<>();

    private CodecRegistry registry = CodecRegistries.fromRegistries(new CodecRegistry() {
        @Override
        public <T> Codec<T> get(Class<T> clazz) {
            return (Codec<T>) codecs.get(clazz);
        }
    }, MongoClientSettings.getDefaultCodecRegistry());

    public DataCodecRegistry() {
    }

    public DataCodecRegistry(String packageName) {
        register(packageName);
    }

    /**
     * 注册指定包名下面所有的编解码器
     *
     * @param packageName 编解码器所在的包
     */
    public void register(String packageName) {
        Set<Class<?>> codecClasses = ClassUtils.loadClasses(packageName, EntityCodec.class);
        for (Class<?> codecClass : codecClasses) {
            if (Modifier.isAbstract(codecClass.getModifiers())) {
                continue;
            }
            try {
                Codec codec = (Codec) codecClass.getDeclaredConstructor(CodecRegistry.class).newInstance(this);
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

    public void test() {
        System.err.println(codecs);
    }
}
