package quan.message.role;

import quan.message.*;
import java.util.*;

/**
 * 角色信息<br/>
 * 代码自动生成，请勿手动修改
 */
public class RoleInfo extends Bean {

    //角色id
    private int id;

    //角色名
    private String name = "";

    private RoleType type;

    private boolean b;

    private short s;

    private int i;

    private double d;

    private byte[] data = new byte[0];

    private List<Integer> list = new ArrayList<>();

    private Set<Integer> set = new HashSet<>();

    private Map<Integer, Integer> map = new HashMap<>();


    /**
     * 角色id
     */
    public int getId() {
        return id;
    }

    /**
     * 角色id
     */
    public RoleInfo setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * 角色名
     */
    public String getName() {
        return name;
    }

    /**
     * 角色名
     */
    public RoleInfo setName(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }

    public RoleType getType() {
        return type;
    }

    public RoleInfo setType(RoleType type) {
        this.type = type;
        return this;
    }

    public boolean getB() {
        return b;
    }

    public RoleInfo setB(boolean b) {
        this.b = b;
        return this;
    }

    public short getS() {
        return s;
    }

    public RoleInfo setS(short s) {
        this.s = s;
        return this;
    }

    public int getI() {
        return i;
    }

    public RoleInfo setI(int i) {
        this.i = i;
        return this;
    }

    public double getD() {
        return d;
    }

    public RoleInfo setD(double d) {
        this.d = d;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public RoleInfo setData(byte[] data) {
        Objects.requireNonNull(data);
        this.data = data;
        return this;
    }

    public List<Integer> getList() {
        return list;
    }

    public Set<Integer> getSet() {
        return set;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    @Override
    public void encode(Buffer buffer) {
        super.encode(buffer);

        buffer.writeInt(this.id);
        buffer.writeString(this.name);
        buffer.writeInt(this.type == null ? 0 : this.type.value());
        buffer.writeBool(this.b);
        buffer.writeShort(this.s);
        buffer.writeInt(this.i);
        buffer.writeDouble(this.d);
        buffer.writeBytes(this.data);

        buffer.writeInt(this.list.size());
        for (Integer list$Value : this.list) {
            buffer.writeInt(list$Value);
        }

        buffer.writeInt(this.set.size());
        for (Integer set$Value : this.set) {
            buffer.writeInt(set$Value);
        }
    }

    @Override
    public void decode(Buffer buffer) {
        super.decode(buffer);

        this.id = buffer.readInt();
        this.name = buffer.readString();
        this.type = RoleType.valueOf(buffer.readInt());
        this.b = buffer.readBool();
        this.s = buffer.readShort();
        this.i = buffer.readInt();
        this.d = buffer.readDouble();
        this.data = buffer.readBytes();

        int list$Size = buffer.readInt();
        for (int i = 0; i < list$Size; i++) {
            this.list.add(buffer.readInt());
        }

        int set$Size = buffer.readInt();
        for (int i = 0; i < set$Size; i++) {
            this.set.add(buffer.readInt());
        }
    }

    @Override
    public String toString() {
        return "RoleInfo{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",type=" + type +
                ",b=" + b +
                ",s=" + s +
                ",i=" + i +
                ",d=" + d +
                ",data=" + Arrays.toString(data) +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                '}';

    }

}
