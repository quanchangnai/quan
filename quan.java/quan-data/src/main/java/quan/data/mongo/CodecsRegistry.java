package quan.data.mongo;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quan.data.Bean;
import quan.data.Data;
import quan.util.ClassUtils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 编解码器注册表，支持注册指定包名下面所有的编解码器
 */
@SuppressWarnings("unchecked")
public class CodecsRegistry implements CodecRegistry {

    protected final static Logger logger = LoggerFactory.getLogger(CodecsRegistry.class);

    private static CodecRegistry registry = new CodecsRegistry();

    private Map<Class<?>, Codec<?>> codecs = new HashMap<>();

    public CodecsRegistry() {
    }

    public CodecsRegistry(String codecsPackage) {
        register(codecsPackage);
    }

    /**
     * 注册指定包名下面所有的编解码器
     *
     * @param codecsPackage 编解码器所在的包
     */
    public void register(String codecsPackage) {
        Objects.requireNonNull(codecsPackage, "编解码器所在包[codecPackage]不能为空");
        Set<Class<?>> codecClasses = ClassUtils.loadClasses(codecsPackage, Codec.class);
        for (Class<?> codecClass : codecClasses) {
            if (Modifier.isAbstract(codecClass.getModifiers())) {
                continue;
            }
            try {
                Codec<?> codec = (Codec<?>) codecClass.getDeclaredConstructor(CodecRegistry.class).newInstance(this);
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
        Codec<T> codec = (Codec<T>) codecs.get(clazz);
        if (codec == null && (Data.class.isAssignableFrom(clazz) || Bean.class.isAssignableFrom(clazz))) {
            register(clazz.getPackage().getName());
            codec = (Codec<T>) codecs.get(clazz);
        }
        return codec;
    }

    public static CodecRegistry getDefault() {
        return registry;
    }

}
