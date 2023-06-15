package quan.message.user;

import java.util.*;
import quan.message.*;

/**
 * 用户信息<br/>
 * 代码自动生成，请勿手动修改
 */
public class UserInfo extends Bean {

    //ID
    private int id;

    //名字
    private String name = "";

    //等级
    private int level;

    //类型
    private UserType type;

    //角色信息
    private quan.message.role.RoleInfo roleInfo1;

    //角色信息2
    private RoleInfo roleInfo2 = new RoleInfo();

    //角色信息2
    private RoleInfo roleInfo3 = new RoleInfo();

    //角色信息List
    private final List<quan.message.role.RoleInfo> roleList = new ArrayList<>();

    //角色信息Set
    private final Set<quan.message.role.RoleInfo> roleSet = new HashSet<>();

    //角色信息Map
    private final Map<Integer, quan.message.role.RoleInfo> roleMap = new HashMap<>();

    private byte[] f11 = new byte[0];

    private boolean f12;

    private boolean f13;

    private short f14;

    private float f15;

    private float f16;

    private double f17;

    private double f18;

    private String alias;


    /**
     * ID
     */
    public int getId() {
        return id;
    }

    /**
     * ID
     */
    public UserInfo setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * 名字
     */
    public String getName() {
        return name;
    }

    /**
     * 名字
     */
    public UserInfo setName(String name) {
        Objects.requireNonNull(name,"参数[name]不能为空");
        this.name = name;
        return this;
    }

    /**
     * 等级
     */
    public int getLevel() {
        return level;
    }

    /**
     * 等级
     */
    public UserInfo setLevel(int level) {
        this.level = level;
        return this;
    }

    /**
     * 类型
     */
    public UserType getType() {
        return type;
    }

    /**
     * 类型
     */
    public UserInfo setType(UserType type) {
        this.type = type;
        return this;
    }

    /**
     * 角色信息
     */
    public quan.message.role.RoleInfo getRoleInfo1() {
        return roleInfo1;
    }

    /**
     * 角色信息
     */
    public UserInfo setRoleInfo1(quan.message.role.RoleInfo roleInfo1) {
        this.roleInfo1 = roleInfo1;
        return this;
    }

    /**
     * 角色信息2
     */
    public RoleInfo getRoleInfo2() {
        return roleInfo2;
    }

    /**
     * 角色信息2
     */
    public UserInfo setRoleInfo2(RoleInfo roleInfo2) {
        Objects.requireNonNull(roleInfo2,"参数[roleInfo2]不能为空");
        this.roleInfo2 = roleInfo2;
        return this;
    }

    /**
     * 角色信息2
     */
    public RoleInfo getRoleInfo3() {
        return roleInfo3;
    }

    /**
     * 角色信息2
     */
    public UserInfo setRoleInfo3(RoleInfo roleInfo3) {
        Objects.requireNonNull(roleInfo3,"参数[roleInfo3]不能为空");
        this.roleInfo3 = roleInfo3;
        return this;
    }

    /**
     * 角色信息List
     */
    public List<quan.message.role.RoleInfo> getRoleList() {
        return roleList;
    }

    /**
     * 角色信息Set
     */
    public Set<quan.message.role.RoleInfo> getRoleSet() {
        return roleSet;
    }

    /**
     * 角色信息Map
     */
    public Map<Integer, quan.message.role.RoleInfo> getRoleMap() {
        return roleMap;
    }

    public byte[] getF11() {
        return f11;
    }

    public UserInfo setF11(byte[] f11) {
        Objects.requireNonNull(f11,"参数[f11]不能为空");
        this.f11 = f11;
        return this;
    }

    public boolean getF12() {
        return f12;
    }

    public UserInfo setF12(boolean f12) {
        this.f12 = f12;
        return this;
    }

    public boolean getF13() {
        return f13;
    }

    public UserInfo setF13(boolean f13) {
        this.f13 = f13;
        return this;
    }

    public short getF14() {
        return f14;
    }

    public UserInfo setF14(short f14) {
        this.f14 = f14;
        return this;
    }

    public float getF15() {
        return f15;
    }

    public UserInfo setF15(float f15) {
        this.f15 = f15;
        return this;
    }

    public float getF16() {
        return f16;
    }

    public UserInfo setF16(float f16) {
        CodedBuffer.validateScale(f16, 2);
        this.f16 = f16;
        return this;
    }

    public double getF17() {
        return f17;
    }

    public UserInfo setF17(double f17) {
        this.f17 = f17;
        return this;
    }

    public double getF18() {
        return f18;
    }

    public UserInfo setF18(double f18) {
        CodedBuffer.validateScale(f18, 2);
        this.f18 = f18;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public UserInfo setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public void encode(CodedBuffer buffer) {
        super.encode(buffer);
        
        validate();

        if (this.id != 0) {
            writeTag(buffer, 4);
            buffer.writeInt(this.id);
        }

        if (!this.name.isEmpty()) {
            writeTag(buffer, 11);
            buffer.writeString(this.name);
        }

        if (this.level != 0) {
            writeTag(buffer, 12);
            buffer.writeInt(this.level);
        }

        if (this.type != null) {
            writeTag(buffer, 16);
            buffer.writeInt(this.type.value);
        }

        if (this.roleInfo1 != null) {
            writeTag(buffer, 23);
            this.roleInfo1.encode(buffer.getTemp());
            buffer.writeTemp();
        }

        writeTag(buffer, 27);
        this.roleInfo2.encode(buffer.getTemp());
        buffer.writeTemp();

        writeTag(buffer, 31);
        this.roleInfo3.encode(buffer.getTemp());
        buffer.writeTemp();

        if (!this.roleList.isEmpty()) {
            writeTag(buffer, 35);
            buffer.getTemp().writeInt(this.roleList.size());
            for (quan.message.role.RoleInfo roleList$Value : this.roleList) {
                roleList$Value.encode(buffer.getTemp());
            }
            buffer.writeTemp();
        }

        if (!this.roleSet.isEmpty()) {
            writeTag(buffer, 39);
            buffer.getTemp().writeInt(this.roleSet.size());
            for (quan.message.role.RoleInfo roleSet$Value : this.roleSet) {
                roleSet$Value.encode(buffer.getTemp());
            }
            buffer.writeTemp();
        }

        if (!this.roleMap.isEmpty()) {
            writeTag(buffer, 43);
            buffer.getTemp().writeInt(this.roleMap.size());
            for (Integer roleMap$Key : this.roleMap.keySet()) {
                buffer.getTemp().writeInt(roleMap$Key);
                this.roleMap.get(roleMap$Key).encode(buffer.getTemp());
            }
            buffer.writeTemp();
        }

        if (this.f11.length > 0) {
            writeTag(buffer, 47);
            buffer.writeBytes(this.f11);
        }

        if (this.f12) {
            writeTag(buffer, 48);
            buffer.writeBool(this.f12);
        }

        if (this.f13) {
            writeTag(buffer, 52);
            buffer.writeBool(this.f13);
        }

        if (this.f14 != 0) {
            writeTag(buffer, 56);
            buffer.writeShort(this.f14);
        }

        if (this.f15 != 0) {
            writeTag(buffer, 61);
            buffer.writeFloat(this.f15);
        }

        if (this.f16 != 0) {
            writeTag(buffer, 64);
            buffer.writeFloat(this.f16, 2);
        }

        if (this.f17 != 0) {
            writeTag(buffer, 70);
            buffer.writeDouble(this.f17);
        }

        if (this.f18 != 0) {
            writeTag(buffer, 72);
            buffer.writeDouble(this.f18, 2);
        }

        if (this.alias != null) {
            writeTag(buffer, 79);
            buffer.writeString(this.alias);
        }

        writeTag(buffer, 0);
    }

    @Override
    public void decode(CodedBuffer buffer) {
        super.decode(buffer);

        for (int tag = readTag(buffer); tag != 0; tag = readTag(buffer)) {
            switch (tag) {
                case 4:
                    this.id = buffer.readInt();
                    break;
                case 11:
                    this.name = buffer.readString();
                    break;
                case 12:
                    this.level = buffer.readInt();
                    break;
                case 16:
                    this.type = UserType.valueOf(buffer.readInt());
                    break;
                case 23:
                    buffer.readInt();
                    if (this.roleInfo1 == null) {
                        this.roleInfo1 = new quan.message.role.RoleInfo();
                    }
                    this.roleInfo1.decode(buffer);
                    break;
                case 27:
                    buffer.readInt();
                    this.roleInfo2.decode(buffer);
                    break;
                case 31:
                    buffer.readInt();
                    this.roleInfo3.decode(buffer);
                    break;
                case 35:
                    buffer.readInt();
                    int roleList$Size = buffer.readInt();
                    for (int i = 0; i < roleList$Size; i++) {
                        quan.message.role.RoleInfo roleList$Value = new quan.message.role.RoleInfo();
                        roleList$Value.decode(buffer);
                        this.roleList.add(roleList$Value);
                    }
                    break;
                case 39:
                    buffer.readInt();
                    int roleSet$Size = buffer.readInt();
                    for (int i = 0; i < roleSet$Size; i++) {
                        quan.message.role.RoleInfo roleSet$Value = new quan.message.role.RoleInfo();
                        roleSet$Value.decode(buffer);
                        this.roleSet.add(roleSet$Value);
                    }
                    break;
                case 43:
                    buffer.readInt();
                    int roleMap$Size = buffer.readInt();
                    for (int i = 0; i < roleMap$Size; i++) {
                        Integer roleMap$Key = buffer.readInt();
                        quan.message.role.RoleInfo roleMap$Value = new quan.message.role.RoleInfo();
                        roleMap$Value.decode(buffer);
                        this.roleMap.put(roleMap$Key, roleMap$Value);
                    }
                    break;
                case 47:
                    this.f11 = buffer.readBytes();
                    break;
                case 48:
                    this.f12 = buffer.readBool();
                    break;
                case 52:
                    this.f13 = buffer.readBool();
                    break;
                case 56:
                    this.f14 = buffer.readShort();
                    break;
                case 61:
                    this.f15 = buffer.readFloat();
                    break;
                case 64:
                    this.f16 = buffer.readFloat(2);
                    break;
                case 70:
                    this.f17 = buffer.readDouble();
                    break;
                case 72:
                    this.f18 = buffer.readDouble(2);
                    break;
                case 79:
                    this.alias = buffer.readString();
                    break;
                default:
                    skipField(tag, buffer);
            }
        }

        validate();
    }

    @Override
    public void validate() {
        super.validate();

        Objects.requireNonNull(name, "字段[name]不能为空");
        Objects.requireNonNull(roleInfo2, "字段[roleInfo2]不能为空");
        Objects.requireNonNull(roleInfo3, "字段[roleInfo3]不能为空");
        Objects.requireNonNull(f11, "字段[f11]不能为空");
        CodedBuffer.validateScale(f16, 2, "字段[f16]");
        CodedBuffer.validateScale(f18, 2, "字段[f18]");
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",level=" + level +
                ",type=" + type +
                ",roleInfo1=" + roleInfo1 +
                ",roleInfo2=" + roleInfo2 +
                ",roleInfo3=" + roleInfo3 +
                ",roleList=" + roleList +
                ",roleSet=" + roleSet +
                ",roleMap=" + roleMap +
                ",f11=" + Arrays.toString(f11) +
                ",f12=" + f12 +
                ",f13=" + f13 +
                ",f14=" + f14 +
                ",f15=" + f15 +
                ",f16=" + f16 +
                ",f17=" + f17 +
                ",f18=" + f18 +
                ",alias='" + alias + '\'' +
                '}';

    }

}
