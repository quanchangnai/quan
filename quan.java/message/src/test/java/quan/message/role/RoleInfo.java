package quan.message.role;

import java.util.*;
import java.io.IOException;
import quan.message.*;

/**
 * 角色信息<br/>
 * 自动生成
 */
public class RoleInfo extends Bean {

    //角色id
    private long id;

    //角色名
    private String roleName = "";

    private RoleType roleType;

    private boolean b;

    private short s;

    private int i;

    private float f;

    private double d;

    private byte[] data = new byte[0];

    private ArrayList<Integer> list = new ArrayList<>();

    private HashSet<Integer> set = new HashSet<>();

    private HashMap<Integer, Integer> map = new HashMap<>();

    public RoleInfo() {
    }

    /**
     * 角色id
     */
    public long getId() {
        return id;
    }

    /**
     * 角色id
     */
    public RoleInfo setId(long id) {
        this.id = id;
        return this;
    }

    /**
     * 角色名
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * 角色名
     */
    public RoleInfo setRoleName(String roleName) {
        Objects.requireNonNull(roleName);
        this.roleName = roleName;
        return this;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public RoleInfo setRoleType(RoleType roleType) {
        this.roleType = roleType;
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

    public float getF() {
        return f;
    }

    public RoleInfo setF(float f) {
        this.f = f;
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

    public ArrayList<Integer> getList() {
        return list;
    }

    public HashSet<Integer> getSet() {
        return set;
    }

    public HashMap<Integer, Integer> getMap() {
        return map;
    }

    @Override
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);

        buffer.writeLong(this.id);
        buffer.writeString(this.roleName);
        buffer.writeInt(this.roleType == null ? 0 : this.roleType.getValue());
        buffer.writeBool(this.b);
        buffer.writeShort(this.s);
        buffer.writeInt(this.i);
        buffer.writeFloat(this.f, 2);
        buffer.writeDouble(this.d);
        buffer.writeBytes(this.data);

        buffer.writeInt(this.list.size());
        for (int $list$Value : this.list) {
            buffer.writeInt($list$Value);
        }

        buffer.writeInt(this.set.size());
        for (int $set$Value : this.set) {
            buffer.writeInt($set$Value);
        }

        buffer.writeInt(this.map.size());
        for (int $map$Key : this.map.keySet()) {
            buffer.writeInt($map$Key);
            buffer.writeInt(this.map.get($map$Key));
        }
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

        this.id = buffer.readLong();
        this.roleName = buffer.readString();
        this.roleType = RoleType.valueOf(buffer.readInt());
        this.b = buffer.readBool();
        this.s = buffer.readShort();
        this.i = buffer.readInt();
        this.f = buffer.readFloat(2);
        this.d = buffer.readDouble();
        this.data = buffer.readBytes();

        int $list$Size = buffer.readInt();
        for (int i = 0; i < $list$Size; i++) {
            this.list.add(buffer.readInt());
        }

        int $set$Size = buffer.readInt();
        for (int i = 0; i < $set$Size; i++) {
            this.set.add(buffer.readInt());
        }

        int $map$Size = buffer.readInt();
        for (int i = 0; i < $map$Size; i++) {
            this.map.put(buffer.readInt(), buffer.readInt());
        }
    }

    @Override
    public String toString() {
        return "RoleInfo{" +
                "id=" + id +
                ",roleName='" + roleName + '\'' +
                ",roleType=" + roleType +
                ",b=" + b +
                ",s=" + s +
                ",i=" + i +
                ",f=" + f +
                ",d=" + d +
                ",data=" + Arrays.toString(data) +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                '}';

    }

}
