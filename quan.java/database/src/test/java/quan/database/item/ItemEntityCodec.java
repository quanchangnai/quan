package quan.database.item;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import quan.database.EntityCodec;

/**
 * Created by quanchangnai on 2020/4/1.
 */
public class ItemEntityCodec extends EntityCodec<ItemEntity> {

    public ItemEntityCodec(CodecRegistry registry) {
        super(registry);
    }

    @Override
    public ItemEntity decode(BsonReader reader, DecoderContext decoderContext) {
        ItemEntity value = new ItemEntity();
        reader.readStartDocument();
        value.setId(reader.readInt32("id"));
        value.setName(reader.readString("name"));
        reader.readEndDocument();
        return value;
    }

    @Override
    public void encode(BsonWriter writer, ItemEntity value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeInt32("id", value.getId());
        writer.writeString("name", value.getName());
        writer.writeEndDocument();
    }

    @Override
    public Class<ItemEntity> getEncoderClass() {
        return ItemEntity.class;
    }
}
