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
import java.util.Objects;
import java.util.Set;

/**
 * 支持注册指定包名下面所有的编解码器的注册表
 * Created by quanchangnai on 2020/4/1.
 */
public class PackageCodecRegistry implements CodecRegistry {

    protected final static Logger logger = LoggerFactory.getLogger(PackageCodecRegistry.class);

    private Map<Class<?>, Codec<?>> codecs = new HashMap<>();

    @SuppressWarnings("unchecked")
    private CodecRegistry registry = CodecRegistries.fromRegistries(new CodecRegistry() {
        public <T> Codec<T> get(Class<T> clazz) {
            return (Codec<T>) codecs.get(clazz);
        }
    }, MongoClientSettings.getDefaultCodecRegistry());

    public PackageCodecRegistry() {
    }

    public PackageCodecRegistry(String codecPackage) {
        register(codecPackage);
    }

    /**
     * 注册指定包名下面所有的编解码器
     *
     * @param codecPackage 编解码器所在的包
     */
    public void register(String codecPackage) {
        Objects.requireNonNull(codecPackage, "编解码器所在包[codecPackage]不能为空");
        Set<Class<?>> codecClasses = ClassUtils.loadClasses(codecPackage, Codec.class);
        for (Class<?> codecClass : codecClasses) {
            if (Modifier.isAbstract(codecClass.getModifiers())) {
                continue;
            }

            try {
                Codec codec = (Codec) codecClass.getDeclaredConstructor(CodecRegistry.class).newInstance(this);
                codecs.put(codec.getEncoderClass(), codec);
            } catch (NoSuchMethodException ignored) {
                //直接忽略
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
