package quan.data.item;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * 道具<br/>
 * 自动生成
 */
public class ItemBean2 {

    public static final String ID = "id";

    public static final String NAME = "name";

    private int id;

    private String name = "";

    private List<Integer> list = new ArrayList<>();

    public ItemBean2() {
    }

    public ItemBean2(int id, String name, List<Integer> list) {
        this.id = id;
        this.name = name;
        this.list.addAll(list);
    }

    public int getId() {
        return id;
    }

    public ItemBean2 setId(int id) {
        this.id = id;
        return this;
    }

    public ItemBean2 addId(int id) {
        setId(getId() + id);
        return this;
    }

    public String getName() {
        return name;
    }

    public ItemBean2 setName(String name) {
        this.name = name;
        return this;
    }

    public List<Integer> getList() {
        return list;
    }

    @Override
    public String toString() {
        return "ItemEntity{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }

    public static class Codec implements org.bson.codecs.Codec<ItemBean2> {

        private CodecRegistry registry;

        public Codec(CodecRegistry registry) {
            this.registry = registry;
        }

        public CodecRegistry getRegistry() {
            return registry;
        }

        @Override
        public ItemBean2 decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            ItemBean2 value = new ItemBean2();

            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                switch (reader.readName()) {
                    case ItemBean2.ID:
                        value.id = reader.readInt32();
                        break;
                    case ItemBean2.NAME:
                        value.name = reader.readString();
                        break;
                    default:
                        reader.skipValue();
                }
            }

            reader.readEndDocument();
            return value;
        }

        @Override
        public void encode(BsonWriter writer, ItemBean2 value, EncoderContext encoderContext) {
            writer.writeStartDocument();

            writer.writeInt32(ItemBean2.ID, value.id);
            writer.writeString(ItemBean2.NAME, value.name);

            writer.writeEndDocument();
        }

        @Override
        public Class<ItemBean2> getEncoderClass() {
            return ItemBean2.class;
        }

    }

}