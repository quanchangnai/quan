package quan.database;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Created by quanchangnai on 2020/4/1.
 */
public abstract class EntityCodec<E extends Entity> implements Codec<E> {

    protected CodecRegistry registry;

    public EntityCodec(CodecRegistry registry) {
        this.registry = registry;
    }

    public CodecRegistry getRegistry() {
        return registry;
    }

}
