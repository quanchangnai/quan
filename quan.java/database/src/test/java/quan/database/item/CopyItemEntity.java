package quan.database.item;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import quan.database.BaseField;
import quan.database.Data;
import quan.database.Entity;
import quan.database.EntityCodec;

/**
 * 道具<br/>
 * 自动生成
 */
public class CopyItemEntity extends Entity {

    private BaseField<Integer> id = new BaseField<>(0);

    private BaseField<String> name = new BaseField<>("");


    public int getId() {
        return id.getValue();
    }

    public CopyItemEntity setId(int id) {
        this.id.setLogValue(id, _getRoot());
        return this;
    }

    public String getName() {
        return name.getValue();
    }

    public CopyItemEntity setName(String name) {
        this.name.setLogValue(name, _getRoot());
        return this;
    }


    @Override
    protected void _setChildrenLogRoot(Data root) {
    }

    @Override
    public String toString() {
        return "ItemEntity{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }


    public static class Codec extends EntityCodec<CopyItemEntity> {

        public Codec(CodecRegistry registry) {
            super(registry);
        }

        @Override
        public CopyItemEntity decode(BsonReader reader, DecoderContext decoderContext) {
            CopyItemEntity value = new CopyItemEntity();
            reader.readStartDocument();
            value.id.setValue(reader.readInt32("id"));
            value.name.setValue(reader.readString("name"));
            reader.readEndDocument();
            return value;
        }

        @Override
        public void encode(BsonWriter writer, CopyItemEntity value, EncoderContext encoderContext) {
            writer.writeStartDocument();
            writer.writeInt32("id", value.id.getValue());
            writer.writeString("name", value.name.getValue());
            writer.writeEndDocument();
        }

        @Override
        public Class<CopyItemEntity> getEncoderClass() {
            return CopyItemEntity.class;
        }
    }

}