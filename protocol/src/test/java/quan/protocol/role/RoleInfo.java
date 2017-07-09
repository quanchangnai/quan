package quan.protocol.role;

import java.util.HashSet;
import java.util.Arrays;
import java.io.IOException;
import quan.protocol.stream.WritableStream;
import java.util.HashMap;
import quan.protocol.Bean;
import quan.protocol.stream.ReadableStream;
import java.util.ArrayList;

/**
 * 角色信息
 * Created by {@link quan.protocol.generator.JavaGenerator}
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
        roleName = "zhangsan";
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
        this.roleName = roleName;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
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
    public void serialize(WritableStream writable) throws IOException {
        writable.writeLong(roleId);
        writable.writeBool(bo);
        writable.writeByte(by);
        writable.writeShort(s);
        writable.writeInt(i);
        writable.writeFloat(f);
        writable.writeDouble(d);
        writable.writeString(roleName);
        writable.writeInt(roleType.getValue());
        writable.writeBytes(data);
        writable.writeInt(list.size());
        for (int listValue : list) {
            writable.writeInt(listValue);
        }
        writable.writeInt(set.size());
        for (int setValue : set) {
            writable.writeInt(setValue);
        }
        writable.writeInt(map.size());
        for (int mapKey : map.keySet()) {
            writable.writeInt(mapKey);
            writable.writeInt(map.get(mapKey));
        }
    }

    @Override
    public void parse(ReadableStream readable) throws IOException {
        roleId = readable.readLong();
        bo = readable.readBool();
        by = readable.readByte();
        s = readable.readShort();
        i = readable.readInt();
        f = readable.readFloat();
        d = readable.readDouble();
        roleName = readable.readString();
        roleType = RoleType.valueOf(readable.readInt());
        data = readable.readBytes();
        int listSize = readable.readInt();
        for (int i = 0; i < listSize; i++) {
            list.add(readable.readInt());
        }
        int setSize = readable.readInt();
        for (int i = 0; i < setSize; i++) {
            set.add(readable.readInt());
        }
        int mapSize = readable.readInt();
        for (int i = 0; i < mapSize; i++) {
            map.put(readable.readInt(), readable.readInt());
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
                ",roleName='" + roleName+ '\'' +
                ",roleType=" + roleType +
                ",data=" + Arrays.toString(data) +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                '}';

    }

}
