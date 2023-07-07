package quan.data.item;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import quan.data.Bean;
import quan.data.Data;
import quan.data.Entity;
import quan.data.Transaction;
import quan.data.field.BaseField;
import quan.data.field.ListField;

import java.util.Collection;
import java.util.List;

/**
 * 道具<br/>
 * 代码自动生成，请勿手动修改
 */
public class ItemBean extends Bean {

    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String LIST = "list";


    private final BaseField<Integer> id = new BaseField<>(0);

    private final BaseField<String> name = new BaseField<>("");

    private final ListField<Integer> list = new ListField<>(_getLogOwner(), _getLogPosition());

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
        this.id.setValue(id, _getLogOwner(), _getLogPosition());
        return this;
    }

    public String getName() {
        return name.getValue();
    }

    public ItemBean setName(String name) {
        this.name.setValue(name, _getLogOwner(), _getLogPosition());
        return this;
    }

    public List<Integer> getList() {
        return list.getDelegate();
    }


    @Override
    protected void _setChildrenLogOwner(Data<?> owner, int position) {
        _setLogOwner(list, owner, position);
    }

    @Override
    public String toString() {
        return "ItemBean{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",list=" + list +
                '}';

    }

    public static ItemBean parseJson(String json) {
        return Entity.parseJson(ItemBean.class, json);
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