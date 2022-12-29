package quan.data.role;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import quan.data.item.ItemBean2;

import java.util.*;

/**
 * 角色<br/>
 * 自动生成
 */
public class RoleData2 {

    public static final String _NAME = "role_data2";

    /**
     * 角色ID
     */
    public static final String ID = "id";

    public static final String NAME = "name";

    /**
     * 角色类型
     */
    public static final String ROLE_TYPE = "roleType";

    public static final String B = "b";

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

    private long id;

    private String name = "";

    private int roleType;

    private boolean b;

    private short s;

    private int i;

    private float f;

    private double d;

    private ItemBean2 item;

    private Map<Integer, ItemBean2> items = new HashMap<>();

    private Set<Boolean> set = new HashSet<>();

    private List<String> list = new ArrayList<>();

    private Map<Integer, Integer> map = new HashMap<>();

    private Set<ItemBean2> set2 = new HashSet<>();

    private List<ItemBean2> list2 = new ArrayList<>();

    private Map<Integer, ItemBean2> map2 = new HashMap<>();


    public RoleData2(Long id) {
        this.id = id;
    }

    /**
     * 主键
     */
    public Long _getId() {
        return id;
    }

    /**
     * 角色ID
     */
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RoleData2 setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 角色类型
     */
    public RoleType getRoleType() {
        return RoleType.valueOf(roleType);
    }

    /**
     * 角色类型
     */
    public RoleData2 setRoleType(RoleType roleType) {
        this.roleType = roleType.value;
        return this;
    }

    public boolean getB() {
        return b;
    }

    public RoleData2 setB(boolean b) {
        this.b = b;
        return this;
    }

    /**
     * sssss
     */
    public short getS() {
        return s;
    }

    /**
     * sssss
     */
    public RoleData2 setS(short s) {
        this.s = s;
        return this;
    }

    /**
     * sssss
     */
    public RoleData2 addS(short s) {
        setS((short) (getS() + s));
        return this;
    }

    /**
     * iiii
     */
    public int getI() {
        return i;
    }

    /**
     * iiii
     */
    public RoleData2 setI(int i) {
        this.i = i;
        return this;
    }

    /**
     * iiii
     */
    public RoleData2 addI(int i) {
        setI(getI() + i);
        return this;
    }

    /**
     * ffff
     */
    public float getF() {
        return f;
    }

    /**
     * ffff
     */
    public RoleData2 setF(float f) {
        this.f = f;
        return this;
    }

    /**
     * ffff
     */
    public RoleData2 addF(float f) {
        setF(getF() + f);
        return this;
    }

    public double getD() {
        return d;
    }

    public RoleData2 setD(double d) {
        this.d = d;
        return this;
    }

    public RoleData2 addD(double d) {
        setD(getD() + d);
        return this;
    }

    /**
     * 道具
     */
    public ItemBean2 getItem() {
        return item;
    }

    /**
     * 道具
     */
    public RoleData2 setItem(ItemBean2 item) {
        this.item = item;
        return this;
    }

    public Map<Integer, ItemBean2> getItems() {
        return items;
    }

    public Set<Boolean> getSet() {
        return set;
    }

    public List<String> getList() {
        return list;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    public Set<ItemBean2> getSet2() {
        return set2;
    }

    public List<ItemBean2> getList2() {
        return list2;
    }

    public Map<Integer, ItemBean2> getMap2() {
        return map2;
    }


    @Override
    public String toString() {
        return "RoleData2{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",roleType=" + RoleType.valueOf(roleType) +
                ",b=" + b +
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

    public static class Codec implements org.bson.codecs.Codec<RoleData2> {

        private CodecRegistry registry;

        public Codec(CodecRegistry registry) {
            this.registry = registry;
        }

        public CodecRegistry getRegistry() {
            return registry;
        }

        @Override
        public RoleData2 decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            RoleData2 value = new RoleData2(reader.readInt64("_id"));

            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                switch (reader.readName()) {
                    case RoleData2.NAME:
                        value.name = reader.readString();
                        break;
                    case RoleData2.ROLE_TYPE:
                        value.roleType = reader.readInt32();
                        break;
                    case RoleData2.B:
                        value.b = reader.readBoolean();
                        break;
                    case RoleData2.S:
                        value.s = (short) reader.readInt32();
                        break;
                    case RoleData2.I:
                        value.i = reader.readInt32();
                        break;
                    case RoleData2.F:
                        value.f = (float) reader.readDouble();
                        break;
                    case RoleData2.D:
                        value.d = reader.readDouble();
                        break;
                    case RoleData2.ITEM:
                        value.item = decoderContext.decodeWithChildContext(registry.get(ItemBean2.class), reader);
                        break;
                    case RoleData2.ITEMS:
                        reader.readStartDocument();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.items.put(reader.readInt32(), decoderContext.decodeWithChildContext(registry.get(ItemBean2.class), reader));
                        }
                        reader.readEndDocument();
                        break;
                    case RoleData2.SET:
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.set.add(reader.readBoolean());
                        }
                        reader.readEndArray();
                        break;
                    case RoleData2.LIST:
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.list.add(reader.readString());
                        }
                        reader.readEndArray();
                        break;
                    case RoleData2.MAP:
                        reader.readStartDocument();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.map.put(reader.readInt32(), reader.readInt32());
                        }
                        reader.readEndDocument();
                        break;
                    case RoleData2.SET2:
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.set2.add(decoderContext.decodeWithChildContext(registry.get(ItemBean2.class), reader));
                        }
                        reader.readEndArray();
                        break;
                    case RoleData2.LIST2:
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.list2.add(decoderContext.decodeWithChildContext(registry.get(ItemBean2.class), reader));
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
        public void encode(BsonWriter writer, RoleData2 value, EncoderContext encoderContext) {
            writer.writeStartDocument();
            writer.writeInt64("_id", value._getId());

            writer.writeString(RoleData2.NAME, value.name);
            writer.writeInt32(RoleData2.ROLE_TYPE, value.roleType);
            writer.writeBoolean(RoleData2.B, value.b);
            writer.writeInt32(RoleData2.S, value.s);
            writer.writeInt32(RoleData2.I, value.i);
            writer.writeDouble(RoleData2.F, value.f);
            writer.writeDouble(RoleData2.D, value.d);

            if (value.item != null) {
                writer.writeName(RoleData2.ITEM);
                encoderContext.encodeWithChildContext(registry.get(ItemBean2.class), writer, value.item);
            }

            if (!value.items.isEmpty()) {
                writer.writeStartDocument(RoleData2.ITEMS);
                for (Integer itemsKey : value.items.keySet()) {
                    writer.writeInt32(itemsKey);
                    encoderContext.encodeWithChildContext(registry.get(ItemBean2.class), writer, value.items.get(itemsKey));
                }
                writer.writeEndDocument();
            }

            if (!value.set.isEmpty()) {
                writer.writeStartArray(RoleData2.SET);
                for (Boolean setValue : value.set) {
                    writer.writeBoolean(setValue);
                }
                writer.writeEndArray();
            }

            if (!value.list.isEmpty()) {
                writer.writeStartArray(RoleData2.LIST);
                for (String listValue : value.list) {
                    writer.writeString(listValue);
                }
                writer.writeEndArray();
            }

            if (!value.map.isEmpty()) {
                writer.writeStartDocument(RoleData2.MAP);
                for (Integer mapKey : value.map.keySet()) {
                    writer.writeInt32(mapKey);
                    writer.writeInt32(value.map.get(mapKey));
                }
                writer.writeEndDocument();
            }

            if (!value.set2.isEmpty()) {
                writer.writeStartArray(RoleData2.SET2);
                for (ItemBean2 set2Value : value.set2) {
                    encoderContext.encodeWithChildContext(registry.get(ItemBean2.class), writer, set2Value);
                }
                writer.writeEndArray();
            }

            if (!value.list2.isEmpty()) {
                writer.writeStartArray(RoleData2.LIST2);
                for (ItemBean2 list2Value : value.list2) {
                    encoderContext.encodeWithChildContext(registry.get(ItemBean2.class), writer, list2Value);
                }
                writer.writeEndArray();
            }

            writer.writeEndDocument();
        }

        @Override
        public Class<RoleData2> getEncoderClass() {
            return RoleData2.class;
        }

    }

}