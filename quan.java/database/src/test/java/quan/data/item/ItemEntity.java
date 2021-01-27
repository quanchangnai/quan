package quan.data.item;

import java.util.*;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistry;
import quan.data.*;
import quan.data.field.*;

/**
 * 道具<br/>
 * 代码自动生成，请勿手动修改
 */
public class ItemEntity extends Entity {

    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String LIST = "list";


    private IntField id = new IntField();

    private StringField name = new StringField();

    private ListField<Integer> list = new ListField<>(_getLogRoot());

    public ItemEntity() {
    }
    
    public ItemEntity(int id, String name, List<Integer> list) {
        this.setId(id);
        this.setName(name);
        this.list.addAll(list);
    }

    public int getId() {
        return id.getLogValue();
    }

    public ItemEntity setId(int id) {
        this.id.setLogValue(id, _getLogRoot());
        return this;
    }

    public ItemEntity addId(int id) {
        setId(getId() + id);
        return this;
    }

    public String getName() {
        return name.getLogValue();
    }

    public ItemEntity setName(String name) {
        this.name.setLogValue(name, _getLogRoot());
        return this;
    }

    public List<Integer> getList() {
        return list.getDelegate();
    }


    @Override
    protected void _setChildrenLogRoot(Data<?> root) {
        _setLogRoot(list, root);
    }

    @Override
    public String toString() {
        return "ItemEntity{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",list=" + list +
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
                    case ItemEntity.LIST:
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.list.plus(reader.readInt32());
                        }
                        reader.readEndArray();
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

            writer.writeInt32(ItemEntity.ID, value.id.getLogValue());
            writer.writeString(ItemEntity.NAME, value.name.getLogValue());

            if (!value.list.isEmpty()) {
                writer.writeStartArray(ItemEntity.LIST);
                for (Integer listValue : value.list) {
                    writer.writeInt32(listValue);
                }
                writer.writeEndArray();
            }

            writer.writeEndDocument();
        }

        @Override
        public Class<ItemEntity> getEncoderClass() {
            return ItemEntity.class;
        }

    }

}