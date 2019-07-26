package quan.message.role;

import java.util.*;
import java.io.IOException;
import quan.message.*;

/**
 * 角色信息<br/>
 * Created by 自动生成
 */
public class RoleInfo extends Bean {

    //角色id
    private long id;

    private boolean b;

    private short s;

    private int i;

    private float f;

    private double d;

    //角色名
    private String roleName = "";

    private RoleType roleType;

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

        buffer.writeLong(id);
        buffer.writeBool(b);
        buffer.writeShort(s);
        buffer.writeInt(i);
        buffer.writeFloat(f);
        buffer.writeDouble(d);
        buffer.writeString(roleName);

        if(roleType != null) {
            buffer.writeInt(roleType.getValue());
        }else {
            buffer.writeInt(0);
        }

        buffer.writeBytes(data);

        buffer.writeInt(list.size());
        for (int $list$Value : list) {
            buffer.writeInt($list$Value);
        }

        buffer.writeInt(set.size());
        for (int $set$Value : set) {
            buffer.writeInt($set$Value);
        }

        buffer.writeInt(map.size());
        for (int $map$Key : map.keySet()) {
            buffer.writeInt($map$Key);
            buffer.writeInt(map.get($map$Key));
        }
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

        id = buffer.readLong();
        b = buffer.readBool();
        s = buffer.readShort();
        i = buffer.readInt();
        f = buffer.readFloat();
        d = buffer.readDouble();
        roleName = buffer.readString();
        roleType = RoleType.valueOf(buffer.readInt());
        data = buffer.readBytes();

        int $list$Size = buffer.readInt();
        for (int i = 0; i < $list$Size; i++) {
            list.add(buffer.readInt());
        }

        int $set$Size = buffer.readInt();
        for (int i = 0; i < $set$Size; i++) {
            set.add(buffer.readInt());
        }

        int $map$Size = buffer.readInt();
        for (int i = 0; i < $map$Size; i++) {
            map.put(buffer.readInt(), buffer.readInt());
        }
    }

    @Override
    public String toString() {
        return "RoleInfo{" +
                "id=" + id +
                ",b=" + b +
                ",s=" + s +
                ",i=" + i +
                ",f=" + f +
                ",d=" + d +
                ",roleName='" + roleName + '\'' +
                ",roleType=" + roleType +
                ",data=" + Arrays.toString(data) +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                '}';

    }

}
