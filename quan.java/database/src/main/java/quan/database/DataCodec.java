package quan.database;


import org.bson.codecs.configuration.CodecRegistry;

/**
 * Created by quanchangnai on 2020/4/1.
 */
public abstract class DataCodec<D extends Data<?>> extends EntityCodec<D> {

    public DataCodec(CodecRegistry registry) {
        super(registry);
    }

}
