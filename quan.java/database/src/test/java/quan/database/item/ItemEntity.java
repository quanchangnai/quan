package quan.database.item;

import java.util.*;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistry;
import quan.database.*;

/**
 * 道具<br/>
 * 自动生成
 */
public class ItemEntity extends Entity {

    private BaseField<Integer> id = new BaseField<>(0);

    private BaseField<String> name = new BaseField<>("");


    public int getId() {
        return id.getValue();
    }

    public ItemEntity setId(int id) {
        this.id.setLogValue(id, _getRoot());
        return this;
    }

    public String getName() {
        return name.getValue();
    }

    public ItemEntity setName(String name) {
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

    public static class Codec extends EntityCodec<ItemEntity> {

        public Codec(CodecRegistry registry) {
            super(registry);
        }

        @Override
        public ItemEntity decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            ItemEntity value = new ItemEntity(); 

            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                switch (reader.readName()) {
                    case "id":
                        value.id.setValue(reader.readInt32());
                        break;
                    case "name":
                        value.name.setValue(reader.readString());
                        break;
                    default:
                        reader.skipValue();
                }
            }

            reader.readEndDocument();
            return value;
        }

        @Override
        public void encode(BsonWriter writer, ItemEntity value, EncoderContext encoderContext) {
            writer.writeStartDocument();

            writer.writeInt32("id", value.id.getValue());
            writer.writeString("name", value.name.getValue());

            writer.writeEndDocument();
        }

        @Override
        public Class<ItemEntity> getEncoderClass() {
            return ItemEntity.class;
        }

    }

}