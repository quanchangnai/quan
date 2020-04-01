package quan.database;


import org.bson.BsonValue;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Created by quanchangnai on 2020/4/1.
 */
public abstract class DataCodec<D extends Data<?>> extends EntityCodec<D> implements CollectibleCodec<D> {

    public DataCodec(CodecRegistry registry) {
        super(registry);
    }

    @Override
    public D generateIdIfAbsentFromDocument(D data) {
        return data;
    }

    @Override
    public boolean documentHasId(D data) {
        return true;
    }

    @Override
    public BsonValue getDocumentId(D data) {
//        BsonDocument idHoldingDocument = new BsonDocument();
//        BsonWriter writer = new BsonDocumentWriter(idHoldingDocument);
//        writer.writeStartDocument();
//        writer.writeName("_id");
//        Object id = data._getId();
//        registry.get((Class<Object>) id.getClass()).encode(writer, id, EncoderContext.builder().build());
//        writer.writeEndDocument();
//        return idHoldingDocument.get("_id");
        return null;
    }
}
