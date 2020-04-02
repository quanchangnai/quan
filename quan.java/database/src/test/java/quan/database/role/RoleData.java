package quan.database.role;

import java.util.*;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistry;
import quan.database.*;
import quan.database.item.ItemEntity;

/**
 * 角色<br/>
 * 自动生成
 */
public class RoleData extends Data<Long> {

    //角色ID
    private BaseField<Long> id = new BaseField<>(0L);

    private BaseField<String> name = new BaseField<>("");

    //角色类型
    private BaseField<Integer> roleType = new BaseField<>(0);

    private BaseField<Boolean> b = new BaseField<>(false);

    private BaseField<Short> s = new BaseField<>((short) 0);

    private BaseField<Integer> i = new BaseField<>(0);

    private BaseField<Float> f = new BaseField<>(0F);

    private BaseField<Double> d = new BaseField<>(0D);

    //道具
    private EntityField<ItemEntity> item = new EntityField<>();

    private MapField<Integer, ItemEntity> items = new MapField<>(_getRoot());

    private SetField<Boolean> set = new SetField<>(_getRoot());

    private ListField<String> list = new ListField<>(_getRoot());

    private MapField<Integer, Integer> map = new MapField<>(_getRoot());

    private SetField<ItemEntity> set2 = new SetField<>(_getRoot());

    private ListField<ItemEntity> list2 = new ListField<>(_getRoot());

    private MapField<Integer, ItemEntity> map2 = new MapField<>(_getRoot());


    public RoleData(Long id) {
        this.id.setLogValue(id, _getRoot());
    }

    /**
     * 主键
     */
    @Override
    public Long _getId() {
        return id.getValue();
    }

    /**
     * 角色ID
     */
    public long getId() {
        return id.getValue();
    }

    public String getName() {
        return name.getValue();
    }

    public RoleData setName(String name) {
        this.name.setLogValue(name, _getRoot());
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
        this.roleType.setLogValue(roleType.value(), _getRoot());
        return this;
    }

    public boolean getB() {
        return b.getValue();
    }

    public RoleData setB(boolean b) {
        this.b.setLogValue(b, _getRoot());
        return this;
    }

    public short getS() {
        return s.getValue();
    }

    public RoleData setS(short s) {
        this.s.setLogValue(s, _getRoot());
        return this;
    }

    public int getI() {
        return i.getValue();
    }

    public RoleData setI(int i) {
        this.i.setLogValue(i, _getRoot());
        return this;
    }

    public float getF() {
        return f.getValue();
    }

    public RoleData setF(float f) {
        this.f.setLogValue(f, _getRoot());
        return this;
    }

    public double getD() {
        return d.getValue();
    }

    public RoleData setD(double d) {
        this.d.setLogValue(d, _getRoot());
        return this;
    }

    /**
     * 道具
     */
    public ItemEntity getItem() {
        return item.getValue();
    }

    /**
     * 道具
     */
    public RoleData setItem(ItemEntity item) {
        this.item.setLogValue(item, _getRoot());
        return this;
    }

    public Map<Integer, ItemEntity> getItems() {
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

    public Set<ItemEntity> getSet2() {
        return set2;
    }

    public List<ItemEntity> getList2() {
        return list2;
    }

    public Map<Integer, ItemEntity> getMap2() {
        return map2;
    }


    @Override
    protected void _setChildrenLogRoot(Data root) {
        ItemEntity $item = this.item.getValue();
        if ($item != null) {
            _setLogRoot($item, root);
        }

        _setLogRoot(items, root);
        _setLogRoot(set, root);
        _setLogRoot(list, root);
        _setLogRoot(map, root);
        _setLogRoot(set2, root);
        _setLogRoot(list2, root);
        _setLogRoot(map2, root);
    }

    @Override
    public String toString() {
        return "RoleData{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",roleType=" + RoleType.valueOf(roleType.getValue()) +
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

    public static class Codec extends EntityCodec<RoleData> {

        public Codec(CodecRegistry registry) {
            super(registry);
        }

        @Override
        public RoleData decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            RoleData value = new RoleData(reader.readInt64("_id"));

            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                switch (reader.readName()) {
                    case "id":
                        value.id.setValue(reader.readInt64());
                        break;
                    case "name":
                        value.name.setValue(reader.readString());
                        break;
                    case "roleType":
                        value.roleType.setValue(reader.readInt32());
                        break;
                    case "b":
                        value.b.setValue(reader.readBoolean());
                        break;
                    case "s":
                        value.s.setValue((short) reader.readInt32());
                        break;
                    case "i":
                        value.i.setValue(reader.readInt32());
                        break;
                    case "f":
                        value.f.setValue((float) reader.readDouble());
                        break;
                    case "d":
                        value.d.setValue(reader.readDouble());
                        break;
                    case "item":
                        value.item.setValue(decoderContext.decodeWithChildContext(registry.get(ItemEntity.class), reader));
                        break;
                    case "items":
                        reader.readStartDocument();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.items.put(reader.readInt32(), decoderContext.decodeWithChildContext(registry.get(ItemEntity.class), reader));
                        }
                        reader.readEndDocument();
                        break;
                    case "set":
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.set.add(reader.readBoolean());
                        }
                        reader.readEndArray();
                        break;
                    case "list":
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.list.add(reader.readString());
                        }
                        reader.readEndArray();
                        break;
                    case "map":
                        reader.readStartDocument();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.map.put(reader.readInt32(), reader.readInt32());
                        }
                        reader.readEndDocument();
                        break;
                    case "set2":
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.set2.add(decoderContext.decodeWithChildContext(registry.get(ItemEntity.class), reader));
                        }
                        reader.readEndArray();
                        break;
                    case "list2":
                        reader.readStartArray();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.list2.add(decoderContext.decodeWithChildContext(registry.get(ItemEntity.class), reader));
                        }
                        reader.readEndArray();
                        break;
                    case "map2":
                        reader.readStartDocument();
                        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                            value.map2.put(reader.readInt32(), decoderContext.decodeWithChildContext(registry.get(ItemEntity.class), reader));
                        }
                        reader.readEndDocument();
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
            writer.writeInt64("_id", value._getId());

            writer.writeInt64("id", value.id.getValue());
            writer.writeString("name", value.name.getValue());
            writer.writeInt32("roleType", value.roleType.getValue());
            writer.writeBoolean("b", value.b.getValue());
            writer.writeInt32("s", value.s.getValue());
            writer.writeInt32("i", value.i.getValue());
            writer.writeDouble("f", value.f.getValue());
            writer.writeDouble("d", value.d.getValue());

            if (value.item.getValue() != null) {
                writer.writeName("item");
                encoderContext.encodeWithChildContext(registry.get(ItemEntity.class), writer, value.item.getValue());
            }

            writer.writeStartDocument("items");
            for (Integer itemsKey : value.items.getValue().keySet()) {
                writer.writeInt32(itemsKey);
                encoderContext.encodeWithChildContext(registry.get(ItemEntity.class), writer, value.items.getValue().get(itemsKey));
            }
            writer.writeEndDocument();

            writer.writeStartArray("set");
            for (Boolean setValue : value.set.getValue()) {
                writer.writeBoolean(setValue);
            }
            writer.writeEndArray();

            writer.writeStartArray("list");
            for (String listValue : value.list.getValue()) {
                writer.writeString(listValue);
            }
            writer.writeEndArray();

            writer.writeStartDocument("map");
            for (Integer mapKey : value.map.getValue().keySet()) {
                writer.writeInt32(mapKey);
                writer.writeInt32(value.map.getValue().get(mapKey));
            }
            writer.writeEndDocument();

            writer.writeStartArray("set2");
            for (ItemEntity set2Value : value.set2.getValue()) {
                encoderContext.encodeWithChildContext(registry.get(ItemEntity.class), writer, set2Value);
            }
            writer.writeEndArray();

            writer.writeStartArray("list2");
            for (ItemEntity list2Value : value.list2.getValue()) {
                encoderContext.encodeWithChildContext(registry.get(ItemEntity.class), writer, list2Value);
            }
            writer.writeEndArray();

            writer.writeStartDocument("map2");
            for (Integer map2Key : value.map2.getValue().keySet()) {
                writer.writeInt32(map2Key);
                encoderContext.encodeWithChildContext(registry.get(ItemEntity.class), writer, value.map2.getValue().get(map2Key));
            }
            writer.writeEndDocument();

            writer.writeEndDocument();
        }

        @Override
        public Class<RoleData> getEncoderClass() {
            return RoleData.class;
        }

    }

}