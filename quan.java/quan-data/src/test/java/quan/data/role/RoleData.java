package quan.data.role;

import java.util.*;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistry;
import quan.data.*;
import quan.data.field.*;
import quan.data.item.ItemBean;
import quan.data.mongo.JsonStringWriter;
import quan.util.NumberUtils;

/**
 * 角色<br/>
 * 代码自动生成，请勿手动修改
 */
@Index(name = "aa", fields = {RoleData.A, RoleData.A2}, type = Index.Type.NORMAL)
@Index(name = "bb", fields = {RoleData.B, RoleData.B2}, type = Index.Type.NORMAL)
@Index(name = "name", fields = {RoleData.NAME, RoleData.NAME2}, type = Index.Type.TEXT)
public class RoleData extends Data<Long> {

    /**
     * 对应的表名
     */
    public static final String _NAME = "role_data";


    /**
     * 角色ID
     */
    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String NAME2 = "name2";

    /**
     * 角色类型
     */
    public static final String ROLE_TYPE = "roleType";

    public static final String A = "a";

    public static final String A2 = "a2";

    public static final String B = "b";

    public static final String B2 = "b2";

    /**
     * sssss
     */
    public static final String S = "s";

    /**
     * iiii
     */
    public static final String I = "i";

    /**
     * ffff
     */
    public static final String F = "f";

    public static final String D = "d";

    /**
     * 道具
     */
    public static final String ITEM = "item";

    public static final String ITEMS = "items";

    public static final String SET = "set";

    public static final String LIST = "list";

    public static final String MAP = "map";

    public static final String SET2 = "set2";

    public static final String LIST2 = "list2";

    public static final String MAP2 = "map2";


    private final LongField id = new LongField();

    private final StringField name = new StringField();

    private final StringField name2 = new StringField();

    private final IntField roleType = new IntField();

    private final IntField a = new IntField();

    private final IntField a2 = new IntField();

    private final BoolField b = new BoolField();

    private final IntField b2 = new IntField();

    private final ShortField s = new ShortField();

    private final IntField i = new IntField();

    private final FloatField f = new FloatField();

    private final DoubleField d = new DoubleField();

    private final BeanField<ItemBean> item = new BeanField<>();

    private final MapField<Integer, ItemBean> items = new MapField<>(this);

    private final SetField<Boolean> set = new SetField<>(this);

    private final ListField<String> list = new ListField<>(this);

    private final MapField<Integer, Integer> map = new MapField<>(this);

    private final SetField<ItemBean> set2 = new SetField<>(this);

    private final ListField<ItemBean> list2 = new ListField<>(this);

    private final MapField<Integer, ItemBean> map2 = new MapField<>(this);

    public RoleData() {
    }

    public RoleData(long id) {
        this.id.setValue(id,this);
    }

    /**
     * 主键
     */
    @Override
    public Long id() {
        return id.getValue();
    }


    /**
     * 角色ID
     */
    public long getId() {
        return id.getValue();
    }

    /**
     * 角色ID
     */
    public RoleData setId(long id) {
        this.id.setValue(id, this);
        return this;
    }

    public String getName() {
        return name.getValue();
    }

    public RoleData setName(String name) {
        this.name.setValue(name, this);
        return this;
    }

    public String getName2() {
        return name2.getValue();
    }

    public RoleData setName2(String name2) {
        this.name2.setValue(name2, this);
        return this;
    }

    /**
     * 角色类型
     */
    public RoleType getRoleType() {
        return RoleType.valueOf(roleType.getValue());
    }

    /**
     * 角色类型
     */
    public RoleData setRoleType(RoleType roleType) {
        this.roleType.setValue(roleType.value, this);
        return this;
    }

    public int getA() {
        return a.getValue();
    }

    public RoleData setA(int a) {
        this.a.setValue(a, this);
        return this;
    }

    public int getA2() {
        return a2.getValue();
    }

    public RoleData setA2(int a2) {
        this.a2.setValue(a2, this);
        return this;
    }

    public boolean getB() {
        return b.getValue();
    }

    public RoleData setB(boolean b) {
        this.b.setValue(b, this);
        return this;
    }

    public int getB2() {
        return b2.getValue();
    }

    public RoleData setB2(int b2) {
        NumberUtils.validateRange(b2, 1, 20, "参数[b2]");
        this.b2.setValue(b2, this);
        return this;
    }

    /**
     * sssss
     */
    public short getS() {
        return s.getValue();
    }

    /**
     * sssss
     */
    public RoleData setS(short s) {
        NumberUtils.validateRange(s, 1, 20, "参数[s]");
        this.s.setValue(s, this);
        return this;
    }

    /**
     * iiii
     */
    public int getI() {
        return i.getValue();
    }

    /**
     * iiii
     */
    public RoleData setI(int i) {
        this.i.setValue(i, this);
        return this;
    }

    /**
     * ffff
     */
    public float getF() {
        return f.getValue();
    }

    /**
     * ffff
     */
    public RoleData setF(float f) {
        this.f.setValue(f, this);
        return this;
    }

    public double getD() {
        return d.getValue();
    }

    public RoleData setD(double d) {
        NumberUtils.validateRange(d, 1, 20, "参数[d]");
        this.d.setValue(d, this);
        return this;
    }

    /**
     * 道具
     */
    public ItemBean getItem() {
        return item.getValue();
    }

    /**
     * 道具
     */
    public RoleData setItem(ItemBean item) {
        this.item.setValue(item, this);
        return this;
    }

    public Map<Integer, ItemBean> getItems() {
        return items.getDelegate();
    }

    public Set<Boolean> getSet() {
        return set.getDelegate();
    }

    public List<String> getList() {
        return list.getDelegate();
    }

    public Map<Integer, Integer> getMap() {
        return map.getDelegate();
    }

    public Set<ItemBean> getSet2() {
        return set2.getDelegate();
    }

    public List<ItemBean> getList2() {
        return list2.getDelegate();
    }

    public Map<Integer, ItemBean> getMap2() {
        return map2.getDelegate();
    }


    @Override
    public String toString() {
        return "RoleData{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",name2='" + name2 + '\'' +
                ",roleType=" + RoleType.valueOf(roleType.getValue()) +
                ",a=" + a +
                ",a2=" + a2 +
                ",b=" + b +
                ",b2=" + b2 +
                ",s=" + s +
                ",i=" + i +
                ",f=" + f +
                ",d=" + d +
                ",item=" + item +
                ",items=" + items +
                ",set=" + set +
                ",list=" + list +
                ",map=" + map +
                ",set2=" + set2 +
                ",list2=" + list2 +
                ",map2=" + map2 +
                '}';

    }

    public static class CodecImpl implements Codec<RoleData> {

        private final CodecRegistry registry;

        public CodecImpl(CodecRegistry registry) {
            this.registry = registry;
        }

        public CodecRegistry getRegistry() {
            return registry;
        }

        @Override
        public RoleData decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            RoleData value = new RoleData(); 
        
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                switch (reader.readName()) {
                    case RoleData._ID:
                    case RoleData.ID:
                        value.id.setValue(reader.readInt64());
                        break;
                    case RoleData.NAME:
                        value.name.setValue(reader.readString());
                        break;
                    case RoleData.NAME2:
                        value.name2.setValue(reader.readString());
                        break;
                    case RoleData.ROLE_TYPE:
                        value.roleType.setValue(reader.readInt32());
                        break;
                    case RoleData.A:
                        value.a.setValue(reader.readInt32());
                        break;
                    case RoleData.A2:
                        value.a2.setValue(reader.readInt32());
                        break;
                    case RoleData.B:
                        value.b.setValue(reader.readBoolean());
                        break;
                    case RoleData.B2:
                        value.b2.setValue(reader.readInt32());
                        break;
                    case RoleData.S:
                        value.s.setValue((short) reader.readInt32());
                        break;
                    case RoleData.I:
                        value.i.setValue(reader.readInt32());
                        break;
                    case RoleData.F:
                        value.f.setValue((float) reader.readDouble());
                        break;
                    case RoleData.D:
                        value.d.setValue(reader.readDouble());
                        break;
                    case RoleData.ITEM:
                        value.item.setValue(decoderContext.decodeWithChildContext(registry.get(ItemBean.class), reader));
                        break;
                    case RoleData.ITEMS:
                        reader.readStartDocument();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.items.plus(Integer.valueOf(reader.readName()), decoderContext.decodeWithChildContext(registry.get(ItemBean.class), reader));
                        }
                        reader.readEndDocument();
                        break;
                    case RoleData.SET:
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.set.plus(reader.readBoolean());
                        }
                        reader.readEndArray();
                        break;
                    case RoleData.LIST:
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.list.plus(reader.readString());
                        }
                        reader.readEndArray();
                        break;
                    case RoleData.MAP:
                        reader.readStartDocument();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.map.plus(Integer.valueOf(reader.readName()), reader.readInt32());
                        }
                        reader.readEndDocument();
                        break;
                    case RoleData.SET2:
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.set2.plus(decoderContext.decodeWithChildContext(registry.get(ItemBean.class), reader));
                        }
                        reader.readEndArray();
                        break;
                    case RoleData.LIST2:
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.list2.plus(decoderContext.decodeWithChildContext(registry.get(ItemBean.class), reader));
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
        public void encode(BsonWriter writer, RoleData value, EncoderContext encoderContext) {
            Transaction transaction = Transaction.get();
            writer.writeStartDocument();

            if (writer instanceof JsonStringWriter) {
                writer.writeInt64(RoleData.ID, value.id.getValue(transaction));
            } else {
                writer.writeInt64(RoleData._ID, value.id.getValue(transaction));
            }

            writer.writeString(RoleData.NAME, value.name.getValue(transaction));
            writer.writeString(RoleData.NAME2, value.name2.getValue(transaction));
            writer.writeInt32(RoleData.ROLE_TYPE, value.roleType.getValue(transaction));
            writer.writeInt32(RoleData.A, value.a.getValue(transaction));
            writer.writeInt32(RoleData.A2, value.a2.getValue(transaction));
            writer.writeBoolean(RoleData.B, value.b.getValue(transaction));
            writer.writeInt32(RoleData.B2, value.b2.getValue(transaction));
            writer.writeInt32(RoleData.S, value.s.getValue(transaction));
            writer.writeInt32(RoleData.I, value.i.getValue(transaction));
            writer.writeDouble(RoleData.F, value.f.getValue(transaction));
            writer.writeDouble(RoleData.D, value.d.getValue(transaction));

            ItemBean $item = value.item.getValue(transaction);
            if ($item != null) {
                writer.writeName(RoleData.ITEM);
                encoderContext.encodeWithChildContext(registry.get(ItemBean.class), writer, $item);
            }

            Map<Integer, ItemBean> $items = value.items.getCurrent(transaction);
            if (!$items.isEmpty()) {
                writer.writeStartDocument(RoleData.ITEMS);
                for (Map.Entry<Integer, ItemBean> itemsEntry : $items.entrySet()) {
                    writer.writeName(String.valueOf(itemsEntry.getKey()));
                    encoderContext.encodeWithChildContext(registry.get(ItemBean.class), writer, itemsEntry.getValue());
                }
                writer.writeEndDocument();
            }

            Collection<Boolean> $set = value.set.getCurrent(transaction);
            if (!$set.isEmpty()) {
                writer.writeStartArray(RoleData.SET);
                for (Boolean setValue : $set) {
                    writer.writeBoolean(setValue);
                }
                writer.writeEndArray();
            }

            Collection<String> $list = value.list.getCurrent(transaction);
            if (!$list.isEmpty()) {
                writer.writeStartArray(RoleData.LIST);
                for (String listValue : $list) {
                    writer.writeString(listValue);
                }
                writer.writeEndArray();
            }

            Map<Integer, Integer> $map = value.map.getCurrent(transaction);
            if (!$map.isEmpty()) {
                writer.writeStartDocument(RoleData.MAP);
                for (Map.Entry<Integer, Integer> mapEntry : $map.entrySet()) {
                    writer.writeName(String.valueOf(mapEntry.getKey()));
                    writer.writeInt32(mapEntry.getValue());
                }
                writer.writeEndDocument();
            }

            Collection<ItemBean> $set2 = value.set2.getCurrent(transaction);
            if (!$set2.isEmpty()) {
                writer.writeStartArray(RoleData.SET2);
                for (ItemBean set2Value : $set2) {
                    encoderContext.encodeWithChildContext(registry.get(ItemBean.class), writer, set2Value);
                }
                writer.writeEndArray();
            }

            Collection<ItemBean> $list2 = value.list2.getCurrent(transaction);
            if (!$list2.isEmpty()) {
                writer.writeStartArray(RoleData.LIST2);
                for (ItemBean list2Value : $list2) {
                    encoderContext.encodeWithChildContext(registry.get(ItemBean.class), writer, list2Value);
                }
                writer.writeEndArray();
            }

            writer.writeEndDocument();
        }

       @Override
        public Class<RoleData> getEncoderClass() {
            return RoleData.class;
        }

    }

}