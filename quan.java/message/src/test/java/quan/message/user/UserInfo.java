package quan.message.user;

import java.util.*;
import quan.message.*;
import quan.message.role.RoleInfo;

/**
 * 用户信息<br/>
 * 自动生成，请勿修改
 */
public class UserInfo extends Bean {

    //ID
    private long id;

    //名字
    private String name = "";

    //等级
    private int level;

    //角色信息
    private RoleInfo roleInfo1;

    //角色信息2
    private RoleInfo roleInfo2 = new RoleInfo();

    //角色信息List
    private List<RoleInfo> roleList = new ArrayList<>();

    //角色信息Set
    private Set<RoleInfo> roleSet = new HashSet<>();

    //角色信息Map
    private Map<Long, RoleInfo> roleMap = new HashMap<>();


    /**
     * ID
     */
    public long getId() {
        return id;
    }

    /**
     * ID
     */
    public UserInfo setId(long id) {
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
        Objects.requireNonNull(name);
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
     * 角色信息
     */
    public RoleInfo getRoleInfo1() {
        return roleInfo1;
    }

    /**
     * 角色信息
     */
    public UserInfo setRoleInfo1(RoleInfo roleInfo1) {
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
        Objects.requireNonNull(roleInfo2);
        this.roleInfo2 = roleInfo2;
        return this;
    }

    /**
     * 角色信息List
     */
    public List<RoleInfo> getRoleList() {
        return roleList;
    }

    /**
     * 角色信息Set
     */
    public Set<RoleInfo> getRoleSet() {
        return roleSet;
    }

    /**
     * 角色信息Map
     */
    public Map<Long, RoleInfo> getRoleMap() {
        return roleMap;
    }

    @Override
    public void encode(Buffer buffer) {
        super.encode(buffer);

        buffer.writeTag(4);
        buffer.writeLong(this.id);

        buffer.writeTag(11);
        buffer.writeString(this.name);

        buffer.writeTag(12);
        buffer.writeInt(this.level);

        buffer.writeTag(19);
        Buffer roleInfo1$Buffer = new SimpleBuffer();
        roleInfo1$Buffer.writeBool(this.roleInfo1 != null);
        if (this.roleInfo1 != null) {
            this.roleInfo1.encode(roleInfo1$Buffer);
        }
        buffer.writeBuffer(roleInfo1$Buffer);

        buffer.writeTag(23);
        Buffer roleInfo2$Buffer = new SimpleBuffer();
        this.roleInfo2.encode(roleInfo2$Buffer);
        buffer.writeBuffer(roleInfo2$Buffer);

        buffer.writeTag(27);
        Buffer roleList$Buffer = new SimpleBuffer();
        roleList$Buffer.writeInt(this.roleList.size());
        for (RoleInfo roleList$Value : this.roleList) {
            roleList$Value.encode(roleList$Buffer);
        }
        buffer.writeBuffer(roleList$Buffer);

        buffer.writeTag(31);
        Buffer roleSet$Buffer = new SimpleBuffer();
        roleSet$Buffer.writeInt(this.roleSet.size());
        for (RoleInfo roleSet$Value : this.roleSet) {
            roleSet$Value.encode(roleSet$Buffer);
        }
        buffer.writeBuffer(roleSet$Buffer);

        buffer.writeTag(35);
        Buffer roleMap$Buffer = new SimpleBuffer();
        roleMap$Buffer.writeInt(this.roleMap.size());
        for (Long roleMap$Key : this.roleMap.keySet()) {
            roleMap$Buffer.writeLong(roleMap$Key);
            this.roleMap.get(roleMap$Key).encode(roleMap$Buffer);
        }
        buffer.writeBuffer(roleMap$Buffer);

        buffer.writeTag(0);
    }

    @Override
    public void decode(Buffer buffer) {
        super.decode(buffer);

        for (int tag = buffer.readTag(); tag != 0; tag = buffer.readTag()) {
            switch (tag) {
                case 4:
                    this.id = buffer.readLong();
                    break;
                case 11:
                    this.name = buffer.readString();
                    break;
                case 12:
                    this.level = buffer.readInt();
                    break;
                case 19:
                    buffer.readInt();
                    if (buffer.readBool()) {
                        if (this.roleInfo1 == null) {
                            this.roleInfo1 = new RoleInfo();
                        }
                        this.roleInfo1.decode(buffer);
                    }
                    break;
                case 23:
                    buffer.readInt();
                    this.roleInfo2.decode(buffer);
                    break;
                case 27:
                    buffer.readInt();
                    int roleList$Size = buffer.readInt();
                    for (int i = 0; i < roleList$Size; i++) {     
                        RoleInfo roleList$Value = new RoleInfo();
                        roleList$Value.decode(buffer);
                        this.roleList.add(roleList$Value);
                    }
                    break;
                case 31:
                    buffer.readInt();
                    int roleSet$Size = buffer.readInt();
                    for (int i = 0; i < roleSet$Size; i++) {     
                        RoleInfo roleSet$Value = new RoleInfo();
                        roleSet$Value.decode(buffer);
                        this.roleSet.add(roleSet$Value);
                    }
                    break;
                case 35:
                    buffer.readInt();
                    int roleMap$Size = buffer.readInt();
                    for (int i = 0; i < roleMap$Size; i++) {
                        Long roleMap$Key = buffer.readLong();
                        RoleInfo roleMap$Value = new RoleInfo();
                        roleMap$Value.decode(buffer);
                        this.roleMap.put(roleMap$Key, roleMap$Value);
                    }
                    break;
                default:
                    skipField(tag, buffer);
            }
        }
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",level=" + level +
                ",roleInfo1=" + roleInfo1 +
                ",roleInfo2=" + roleInfo2 +
                ",roleList=" + roleList +
                ",roleSet=" + roleSet +
                ",roleMap=" + roleMap +
                '}';

    }

}
