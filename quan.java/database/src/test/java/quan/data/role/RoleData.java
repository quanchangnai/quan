package quan.data.role;

import java.util.*;

import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistry;
import quan.data.*;
import quan.data.field.*;
import quan.data.mongo.JsonStringWriter;
import quan.data.item.ItemEntity;

/**
 * 角色<br/>
 * 代码自动生成，请勿手动修改
 */
@Index(name = "aa", fields = {RoleData.A, RoleData.A2}, unique = false)
@Index(name = "bb", fields = {RoleData.B, RoleData.B2}, unique = false)
@Index(name = "name", fields = {RoleData.NAME}, unique = true)
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


    private LongField id = new LongField();

    private StringField name = new StringField();

    private IntField roleType = new IntField();

    private IntField a = new IntField();

    private IntField a2 = new IntField();

    private BoolField b = new BoolField();

    private IntField b2 = new IntField();

    private ShortField s = new ShortField();

    private IntField i = new IntField();

    private FloatField f = new FloatField();

    private DoubleField d = new DoubleField();

    private EntityField<ItemEntity> item = new EntityField<>();

    private MapField<Integer, ItemEntity> items = new MapField<>(this);

    private SetField<Boolean> set = new SetField<>(this);

    private ListField<String> list = new ListField<>(this);

    private MapField<Integer, Integer> map = new MapField<>(this);

    private SetField<ItemEntity> set2 = new SetField<>(this);

    private ListField<ItemEntity> list2 = new ListField<>(this);

    private MapField<Integer, ItemEntity> map2 = new MapField<>(this);

    public RoleData() {
    }

    public RoleData(long id) {
        this.id.setLogValue(id, this);
    }

    /**
     * 表名
     */
    @Override
    public String _name() {
        return _NAME;
    }

    /**
     * 主键(_id)
     */
    @Override
    public Long _id() {
        return id.getLogValue();
    }


    /**
     * 角色ID
     */
    public long getId() {
        return id.getLogValue();
    }

    /**
     * 角色ID
     */
    public RoleData setId(long id) {
        this.id.setLogValue(id, this);
        return this;
    }

    /**
     * 角色ID
     */
    public RoleData addId(long id) {
        setId(getId() + id);
        return this;
    }

    public String getName() {
        return name.getLogValue();
    }

    public RoleData setName(String name) {
        this.name.setLogValue(name, this);
        return this;
    }

    /**
     * 角色类型
     */
    public RoleType getRoleType() {
        return RoleType.valueOf(roleType.getLogValue());
    }

    /**
     * 角色类型
     */
    public RoleData setRoleType(RoleType roleType) {
        this.roleType.setLogValue(roleType.value(), this);
        return this;
    }

    public int getA() {
        return a.getLogValue();
    }

    public RoleData setA(int a) {
        this.a.setLogValue(a, this);
        return this;
    }

    public RoleData addA(int a) {
        setA(getA() + a);
        return this;
    }

    public int getA2() {
        return a2.getLogValue();
    }

    public RoleData setA2(int a2) {
        this.a2.setLogValue(a2, this);
        return this;
    }

    public RoleData addA2(int a2) {
        setA2(getA2() + a2);
        return this;
    }

    public boolean getB() {
        return b.getLogValue();
    }

    public RoleData setB(boolean b) {
        this.b.setLogValue(b, this);
        return this;
    }

    public int getB2() {
        return b2.getLogValue();
    }

    public RoleData setB2(int b2) {
        this.b2.setLogValue(b2, this);
        return this;
    }

    public RoleData addB2(int b2) {
        setB2(getB2() + b2);
        return this;
    }

    /**
     * sssss
     */
    public short getS() {
        return s.getLogValue();
    }

    /**
     * sssss
     */
    public RoleData setS(short s) {
        this.s.setLogValue(s, this);
        return this;
    }

    /**
     * sssss
     */
    public RoleData addS(short s) {
        setS((short) (getS() + s));
        return this;
    }

    /**
     * iiii
     */
    public int getI() {
        return i.getLogValue();
    }

    /**
     * iiii
     */
    public RoleData setI(int i) {
        this.i.setLogValue(i, this);
        return this;
    }

    /**
     * iiii
     */
    public RoleData addI(int i) {
        setI(getI() + i);
        return this;
    }

    /**
     * ffff
     */
    public float getF() {
        return f.getLogValue();
    }

    /**
     * ffff
     */
    public RoleData setF(float f) {
        this.f.setLogValue(f, this);
        return this;
    }

    /**
     * ffff
     */
    public RoleData addF(float f) {
        setF(getF() + f);
        return this;
    }

    public double getD() {
        return d.getLogValue();
    }

    public RoleData setD(double d) {
        this.d.setLogValue(d, this);
        return this;
    }

    public RoleData addD(double d) {
        setD(getD() + d);
        return this;
    }

    /**
     * 道具
     */
    public ItemEntity getItem() {
        return item.getLogValue();
    }

    /**
     * 道具
     */
    public RoleData setItem(ItemEntity item) {
        this.item.setLogValue(item, this);
        return this;
    }

    public Map<Integer, ItemEntity> getItems() {
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

    public Set<ItemEntity> getSet2() {
        return set2.getDelegate();
    }

    public List<ItemEntity> getList2() {
        return list2.getDelegate();
    }

    public Map<Integer, ItemEntity> getMap2() {
        return map2.getDelegate();
    }


    @Override
    public String toString() {
        return "RoleData{" +
                "id=" + id +
                ",name='" + name + '\'' +
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

    public static class Codec implements org.bson.codecs.Codec<RoleData> {

        private CodecRegistry registry;

        public Codec(CodecRegistry registry) {
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
                        value.item.setValue(decoderContext.decodeWithChildContext(registry.get(ItemEntity.class), reader));
                        break;
                    case RoleData.ITEMS:
                        reader.readStartDocument();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.items.plus(Integer.valueOf(reader.readName()), decoderContext.decodeWithChildContext(registry.get(ItemEntity.class), reader));
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
                            value.set2.plus(decoderContext.decodeWithChildContext(registry.get(ItemEntity.class), reader));
                        }
                        reader.readEndArray();
                        break;
                    case RoleData.LIST2:
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.list2.plus(decoderContext.decodeWithChildContext(registry.get(ItemEntity.class), reader));
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
            writer.writeStartDocument();

            if (writer instanceof JsonStringWriter) {
                writer.writeInt64(RoleData.ID, value.id.getLogValue());
            } else {
                writer.writeInt64(RoleData._ID, value.id.getLogValue());
            }

            writer.writeString(RoleData.NAME, value.name.getLogValue());
            writer.writeInt32(RoleData.ROLE_TYPE, value.roleType.getLogValue());
            writer.writeInt32(RoleData.A, value.a.getLogValue());
            writer.writeInt32(RoleData.A2, value.a2.getLogValue());
            writer.writeBoolean(RoleData.B, value.b.getLogValue());
            writer.writeInt32(RoleData.B2, value.b2.getLogValue());
            writer.writeInt32(RoleData.S, value.s.getLogValue());
            writer.writeInt32(RoleData.I, value.i.getLogValue());
            writer.writeDouble(RoleData.F, value.f.getLogValue());
            writer.writeDouble(RoleData.D, value.d.getLogValue());

            if (value.item.getLogValue() != null) {
                writer.writeName(RoleData.ITEM);
                encoderContext.encodeWithChildContext(registry.get(ItemEntity.class), writer, value.item.getLogValue());
            }

            if (!value.items.isEmpty()) {
                writer.writeStartDocument(RoleData.ITEMS);
                for (Integer itemsKey : value.items.keySet()) {
                    writer.writeName(String.valueOf(itemsKey));
                    encoderContext.encodeWithChildContext(registry.get(ItemEntity.class), writer, value.items.get(itemsKey));
                }
                writer.writeEndDocument();
            }

            if (!value.set.isEmpty()) {
                writer.writeStartArray(RoleData.SET);
                for (Boolean setValue : value.set) {
                    writer.writeBoolean(setValue);
                }
                writer.writeEndArray();
            }

            if (!value.list.isEmpty()) {
                writer.writeStartArray(RoleData.LIST);
                for (String listValue : value.list) {
                    writer.writeString(listValue);
                }
                writer.writeEndArray();
            }

            if (!value.map.isEmpty()) {
                writer.writeStartDocument(RoleData.MAP);
                for (Integer mapKey : value.map.keySet()) {
                    writer.writeName(String.valueOf(mapKey));
                    writer.writeInt32(value.map.get(mapKey));
                }
                writer.writeEndDocument();
            }

            if (!value.set2.isEmpty()) {
                writer.writeStartArray(RoleData.SET2);
                for (ItemEntity set2Value : value.set2) {
                    encoderContext.encodeWithChildContext(registry.get(ItemEntity.class), writer, set2Value);
                }
                writer.writeEndArray();
            }

            if (!value.list2.isEmpty()) {
                writer.writeStartArray(RoleData.LIST2);
                for (ItemEntity list2Value : value.list2) {
                    encoderContext.encodeWithChildContext(registry.get(ItemEntity.class), writer, list2Value);
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