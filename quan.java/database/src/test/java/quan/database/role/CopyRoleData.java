package quan.database.role;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import quan.database.*;
import quan.database.item.CopyItemEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色<br/>
 * 自动生成
 */
public class CopyRoleData extends Data<Long> {

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
    private EntityField<CopyItemEntity> item = new EntityField<>();

    private MapField<Integer, CopyItemEntity> items = new MapField<>(_getRoot());

    private SetField<Boolean> set = new SetField<>(_getRoot());

    private ListField<String> list = new ListField<>(_getRoot());

    private MapField<Integer, Integer> map = new MapField<>(_getRoot());

    private SetField<CopyItemEntity> set2 = new SetField<>(_getRoot());

    private ListField<CopyItemEntity> list2 = new ListField<>(_getRoot());

    private MapField<Integer, CopyItemEntity> map2 = new MapField<>(_getRoot());


    public CopyRoleData(Long id) {
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

    public CopyRoleData setName(String name) {
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
    public CopyRoleData setRoleType(RoleType roleType) {
        this.roleType.setLogValue(roleType.value(), _getRoot());
        return this;
    }

    public boolean getB() {
        return b.getValue();
    }

    public CopyRoleData setB(boolean b) {
        this.b.setLogValue(b, _getRoot());
        return this;
    }

    public short getS() {
        return s.getValue();
    }

    public CopyRoleData setS(short s) {
        this.s.setLogValue(s, _getRoot());
        return this;
    }

    public int getI() {
        return i.getValue();
    }

    public CopyRoleData setI(int i) {
        this.i.setLogValue(i, _getRoot());
        return this;
    }

    public float getF() {
        return f.getValue();
    }

    public CopyRoleData setF(float f) {
        this.f.setLogValue(f, _getRoot());
        return this;
    }

    public double getD() {
        return d.getValue();
    }

    public CopyRoleData setD(double d) {
        this.d.setLogValue(d, _getRoot());
        return this;
    }

    /**
     * 道具
     */
    public CopyItemEntity getItem() {
        return item.getValue();
    }

    /**
     * 道具
     */
    public CopyRoleData setItem(CopyItemEntity item) {
        this.item.setLogValue(item, _getRoot());
        return this;
    }

    public Map<Integer, CopyItemEntity> getItems() {
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

    public Set<CopyItemEntity> getSet2() {
        return set2;
    }

    public List<CopyItemEntity> getList2() {
        return list2;
    }

    public Map<Integer, CopyItemEntity> getMap2() {
        return map2;
    }


    @Override
    protected void _setChildrenLogRoot(Data root) {
        CopyItemEntity $item = this.item.getValue();
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


    public static class Codec extends DataCodec<CopyRoleData> {

        public Codec(CodecRegistry registry) {
            super(registry);
        }

        @Override
        public CopyRoleData decode(BsonReader reader, DecoderContext decoderContext) {
            reader.readStartDocument();
            CopyRoleData roleData = new CopyRoleData(reader.readInt64("_id"));

            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                String fieldName = reader.readName();
                switch (fieldName) {
                    case "name":
                        roleData.name.setValue(reader.readString());
                        break;
                    case "item":
                        roleData.item.setValue(decoderContext.decodeWithChildContext(registry.get(CopyItemEntity.class), reader));
                        break;
                    default:
                        reader.skipValue();
                }
            }

            reader.readEndDocument();
            return roleData;
        }

        @Override
        public void encode(BsonWriter writer, CopyRoleData value, EncoderContext encoderContext) {
//        System.err.println("RoleDataCodec.encode Thread" + Thread.currentThread());
            writer.writeStartDocument();

            writer.writeInt64("_id", value._getId());
            writer.writeString("name", value.name.getValue());
            if (value.item.getValue() != null) {
                writer.writeName("item");
                encoderContext.encodeWithChildContext(registry.get(CopyItemEntity.class), writer, value.item.getValue() );
            }

            writer.writeStartArray("list");
            for (String s : value.getList()) {
                writer.writeString(s);
            }
            writer.writeEndArray();

            writer.writeEndDocument();
        }

        @Override
        public Class<CopyRoleData> getEncoderClass() {
            return CopyRoleData.class;
        }
    }

}