package quan.generator.message.role;

import quan.message.Buffer;
import java.util.*;
import java.io.IOException;
import quan.message.Bean;

/**
 * 角色信息
 * Created by 自动生成
 */
public class RoleInfo extends Bean {

    private long roleId;//角色id

    private boolean bo;

    private byte by;

    private short s;

    private int i;

    private float f;

    private double d;

    private String roleName;//角色名

    private RoleType roleType;

    private byte[] data;

    private ArrayList<Integer> list;

    private HashSet<Integer> set;

    private HashMap<Integer, Integer> map;

    public RoleInfo() {
        roleId = 111L;
        bo = true;
        by = (byte) 22;
        s = (short) 22;
        i = 11;
        f = 22.33332F;
        d = 33.332432D;
        roleName = "";
        roleType = RoleType.type1;
        data = new byte[0];
        list = new ArrayList<>();
        set = new HashSet<>();
        map = new HashMap<>();
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public boolean getBo() {
        return bo;
    }

    public void setBo(boolean bo) {
        this.bo = bo;
    }

    public byte getBy() {
        return by;
    }

    public void setBy(byte by) {
        this.by = by;
    }

    public short getS() {
        return s;
    }

    public void setS(short s) {
        this.s = s;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        if (roleName == null){
            throw new NullPointerException();
        }
        this.roleName = roleName;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        if (roleType == null){
            throw new NullPointerException();
        }
        this.roleType = roleType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        if (data == null){
            throw new NullPointerException();
        }
        this.data = data;
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
        buffer.writeInt(roleType.getValue());
        buffer.writeBytes(data);
        buffer.writeInt(list.size());
        for (int listValue : list) {
            buffer.writeInt(listValue);
        }
        buffer.writeInt(set.size());
        for (int setValue : set) {
            buffer.writeInt(setValue);
        }
        buffer.writeInt(map.size());
        for (int mapKey : map.keySet()) {
            buffer.writeInt(mapKey);
            buffer.writeInt(map.get(mapKey));
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
        int listSize = buffer.readInt();
        for (int i = 0; i < listSize; i++) {
            list.add(buffer.readInt());
        }
        int setSize = buffer.readInt();
        for (int i = 0; i < setSize; i++) {
            set.add(buffer.readInt());
        }
        int mapSize = buffer.readInt();
        for (int i = 0; i < mapSize; i++) {
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
