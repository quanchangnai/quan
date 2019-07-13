package quan.message.role;

import quan.message.Buffer;
import java.util.*;
import java.io.IOException;
import quan.message.Bean;

/**
 * 角色信息<br/>
 * Created by 自动生成
 */
public class RoleInfo extends Bean {

    //角色id
    private long roleId;

    
    private boolean bo;

    
    private byte by;

    
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

    public long getRoleId() {
        return roleId;
    }

    public RoleInfo setRoleId(long roleId) {
        this.roleId = roleId;
        return this;
    }

    public boolean getBo() {
        return bo;
    }

    public RoleInfo setBo(boolean bo) {
        this.bo = bo;
        return this;
    }

    public byte getBy() {
        return by;
    }

    public RoleInfo setBy(byte by) {
        this.by = by;
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

    public String getRoleName() {
        return roleName;
    }

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

        buffer.writeLong(roleId);
        buffer.writeBool(bo);
        buffer.writeByte(by);
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
        for (int _list_Value : list) {
            buffer.writeInt(_list_Value);
        }

        buffer.writeInt(set.size());
        for (int _set_Value : set) {
            buffer.writeInt(_set_Value);
        }

        buffer.writeInt(map.size());
        for (int _map_Key : map.keySet()) {
            buffer.writeInt(_map_Key);
            buffer.writeInt(map.get(_map_Key));
        }

    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

        roleId = buffer.readLong();
        bo = buffer.readBool();
        by = buffer.readByte();
        s = buffer.readShort();
        i = buffer.readInt();
        f = buffer.readFloat();
        d = buffer.readDouble();
        roleName = buffer.readString();
        roleType = RoleType.valueOf(buffer.readInt());
        data = buffer.readBytes();

        int _list_Size = buffer.readInt();
        for (int i = 0; i < _list_Size; i++) {
            list.add(buffer.readInt());
        }

        int _set_Size = buffer.readInt();
        for (int i = 0; i < _set_Size; i++) {
            set.add(buffer.readInt());
        }

        int _map_Size = buffer.readInt();
        for (int i = 0; i < _map_Size; i++) {
            map.put(buffer.readInt(), buffer.readInt());
        }

    }

    @Override
    public String toString() {
        return "RoleInfo{" +
                "roleId=" + roleId +
                ",bo=" + bo +
                ",by=" + by +
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
