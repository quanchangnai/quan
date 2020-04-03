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

    public static final String ID = "id";

    public static final String NAME = "name";

    private BaseField<Integer> id = new BaseField<>(0);

    private BaseField<String> name = new BaseField<>("");


    public int getId() {
        return id.getValue();
    }

    public ItemEntity setId(int id) {
        this.id.setLogValue(id, _getRoot());
        return this;
    }

    public ItemEntity addId(int id) {
        setId(getId() + id);
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

    public static class Codec implements org.bson.codecs.Codec<ItemEntity> {

        private CodecRegistry registry;

        public Codec(CodecRegistry registry) {
            this.registry = registry;
        }

        public CodecRegistry getRegistry() {
            return registry;
        }

        @Override
        public ItemEntity decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            ItemEntity value = new ItemEntity(); 

            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                switch (reader.readName()) {
                    case ItemEntity.ID:
                        value.id.setValue(reader.readInt32());
                        break;
                    case ItemEntity.NAME:
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

            writer.writeInt32(ItemEntity.ID, value.id.getValue());
            writer.writeString(ItemEntity.NAME, value.name.getValue());

            writer.writeEndDocument();
        }

        @Override
        public Class<ItemEntity> getEncoderClass() {
            return ItemEntity.class;
        }

    }

}