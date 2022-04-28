package quan.message.role;

import quan.message.CodedBuffer;
import quan.message.Message;
import quan.message.user.UserInfo;

import java.util.*;

/**
 * 角色登录，哈希生成ID<br/>
 * 代码自动生成，请勿手动修改
 */
public class SRoleLogin extends Message {

    /**
     * 消息ID
     */
    public static final int ID = 763075;

    //角色id
    private int roleId;

    //角色名
    private String roleName = "";

    //角色信息
    private RoleInfo roleInfo = new RoleInfo();

    //角色信息
    private List<RoleInfo> roleInfoList = new ArrayList<>();

    //角色信息
    private Set<RoleInfo> roleInfoSet = new HashSet<>();

    //角色信息
    private Map<Integer, RoleInfo> roleInfoMap = new HashMap<>();

    //用户信息
    private UserInfo userInfo;


    /**
     * 消息ID
     */
    @Override
    public final int getId() {
        return ID;
    }

    /**
     * 角色id
     */
    public int getRoleId() {
        return roleId;
    }

    /**
     * 角色id
     */
    public SRoleLogin setRoleId(int roleId) {
        this.roleId = roleId;
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
    public SRoleLogin setRoleName(String roleName) {
        Objects.requireNonNull(roleName);
        this.roleName = roleName;
        return this;
    }

    /**
     * 角色信息
     */
    public RoleInfo getRoleInfo() {
        return roleInfo;
    }

    /**
     * 角色信息
     */
    public SRoleLogin setRoleInfo(RoleInfo roleInfo) {
        Objects.requireNonNull(roleInfo);
        this.roleInfo = roleInfo;
        return this;
    }

    /**
     * 角色信息
     */
    public List<RoleInfo> getRoleInfoList() {
        return roleInfoList;
    }

    /**
     * 角色信息
     */
    public Set<RoleInfo> getRoleInfoSet() {
        return roleInfoSet;
    }

    /**
     * 角色信息
     */
    public Map<Integer, RoleInfo> getRoleInfoMap() {
        return roleInfoMap;
    }

    /**
     * 用户信息
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 用户信息
     */
    public SRoleLogin setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    @Override
    public SRoleLogin create() {
        return new SRoleLogin();
    }

    @Override
    public void encode(CodedBuffer buffer) {
        super.encode(buffer);

        buffer.writeInt(this.roleId);
        buffer.writeString(this.roleName);
        this.roleInfo.encode(buffer);

        buffer.writeInt(this.roleInfoList.size());
        for (RoleInfo roleInfoList$Value : this.roleInfoList) {
            roleInfoList$Value.encode(buffer);
        }

        buffer.writeInt(this.roleInfoSet.size());
        for (RoleInfo roleInfoSet$Value : this.roleInfoSet) {
            roleInfoSet$Value.encode(buffer);
        }

        buffer.writeInt(this.roleInfoMap.size());
        for (Integer roleInfoMap$Key : this.roleInfoMap.keySet()) {
            buffer.writeInt(roleInfoMap$Key);
            this.roleInfoMap.get(roleInfoMap$Key).encode(buffer);
        }

        buffer.writeBool(this.userInfo != null);
        if (this.userInfo != null) {
            this.userInfo.encode(buffer);
        }
    }

    @Override
    public void decode(CodedBuffer buffer) {
        super.decode(buffer);

        this.roleId = buffer.readInt();
        this.roleName = buffer.readString();
        this.roleInfo.decode(buffer);

        int roleInfoList$Size = buffer.readInt();
        for (int i = 0; i < roleInfoList$Size; i++) {
            RoleInfo roleInfoList$Value = new RoleInfo();
            roleInfoList$Value.decode(buffer);
            this.roleInfoList.add(roleInfoList$Value);
        }

        int roleInfoSet$Size = buffer.readInt();
        for (int i = 0; i < roleInfoSet$Size; i++) {
            RoleInfo roleInfoSet$Value = new RoleInfo();
            roleInfoSet$Value.decode(buffer);
            this.roleInfoSet.add(roleInfoSet$Value);
        }

        int roleInfoMap$Size = buffer.readInt();
        for (int i = 0; i < roleInfoMap$Size; i++) {
            Integer roleInfoMap$Key = buffer.readInt();
            RoleInfo roleInfoMap$Value = new RoleInfo();
            roleInfoMap$Value.decode(buffer);
            this.roleInfoMap.put(roleInfoMap$Key, roleInfoMap$Value);
        }

        if (buffer.readBool()) {
            if (this.userInfo == null) {
                this.userInfo = new UserInfo();
            }
            this.userInfo.decode(buffer);
        }
    }

    @Override
    public String toString() {
        return "SRoleLogin{" +
                "_id=" + ID +
                ",roleId=" + roleId +
                ",roleName='" + roleName + '\'' +
                ",roleInfo=" + roleInfo +
                ",roleInfoList=" + roleInfoList +
                ",roleInfoSet=" + roleInfoSet +
                ",roleInfoMap=" + roleInfoMap +
                ",userInfo=" + userInfo +
                '}';

    }

}
