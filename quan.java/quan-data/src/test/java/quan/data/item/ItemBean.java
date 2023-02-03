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
public class ItemBean extends Bean {

    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String LIST = "list";


    private final IntField id = new IntField();

    private final StringField name = new StringField();

    private final ListField<Integer> list = new ListField<>(_getLogRoot());

    public ItemBean() {
    }
    
    public ItemBean(int id, String name, List<Integer> list) {
        this.setId(id);
        this.setName(name);
        this.list.addAll(list);
    }

    public int getId() {
        return id.getValue();
    }

    public ItemBean setId(int id) {
        this.id.setValue(id, _getLogRoot());
        return this;
    }

    public String getName() {
        return name.getValue();
    }

    public ItemBean setName(String name) {
        this.name.setValue(name, _getLogRoot());
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
        return "ItemBean{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",list=" + list +
                '}';

    }

    public static class CodecImpl implements Codec<ItemBean> {

        private final CodecRegistry registry;

        public CodecImpl(CodecRegistry registry) {
            this.registry = registry;
        }

        public CodecRegistry getRegistry() {
            return registry;
        }

        @Override
        public ItemBean decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            ItemBean value = new ItemBean(); 
        
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                switch (reader.readName()) {
                    case ItemBean.ID:
                        value.id.setValue(reader.readInt32());
                        break;
                    case ItemBean.NAME:
                        value.name.setValue(reader.readString());
                        break;
                    case ItemBean.LIST:
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
        public void encode(BsonWriter writer, ItemBean value, EncoderContext encoderContext) {
            Transaction transaction = Transaction.get();
            writer.writeStartDocument();

            writer.writeInt32(ItemBean.ID, value.id.getValue(transaction));
            writer.writeString(ItemBean.NAME, value.name.getValue(transaction));

            Collection<Integer> $list = value.list.getCurrent(transaction);
            if (!$list.isEmpty()) {
                writer.writeStartArray(ItemBean.LIST);
                for (Integer listValue : $list) {
                    writer.writeInt32(listValue);
                }
                writer.writeEndArray();
            }

            writer.writeEndDocument();
        }

       @Override
        public Class<ItemBean> getEncoderClass() {
            return ItemBean.class;
        }

    }

}