package quan.database.role;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import quan.database.DataCodec;

/**
 * Created by quanchangnai on 2020/4/1.
 */
public class RoleDataCodec extends DataCodec<RoleData> {

    public RoleDataCodec(CodecRegistry registry) {
        super(registry);
    }

    @Override
    public RoleData decode(BsonReader reader, DecoderContext decoderContext) {
        RoleData roleData = new RoleData(111L);
        return roleData;
    }

    @Override
    public void encode(BsonWriter writer, RoleData value, EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeInt64("_id", value._getId());

        writer.writeEndDocument();
    }

    @Override
    public Class<RoleData> getEncoderClass() {
        return RoleData.class;
    }
}
