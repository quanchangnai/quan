package quan.database;

import org.bson.codecs.Codec;
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
 * 数据编解码器注册表，支持注册指定包名下面所有的编解码器
 * Created by quanchangnai on 2020/4/1.
 */
@SuppressWarnings("unchecked")
public class DataCodecRegistry implements CodecRegistry {

    protected final static Logger logger = LoggerFactory.getLogger(DataCodecRegistry.class);

    private Map<Class<?>, Codec<?>> codecs = new HashMap<>();

    public DataCodecRegistry() {
    }

    public DataCodecRegistry(String codecPackage) {
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
        return (Codec<T>) codecs.get(clazz);
    }

}
