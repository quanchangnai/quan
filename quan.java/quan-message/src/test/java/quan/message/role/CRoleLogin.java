package quan.message.role;

import java.util.*;
import quan.message.*;
import quan.message.user.UserInfo;

/**
 * 角色登录，自定义ID，111，角色登录，自定义ID，222<br/>
 * 代码自动生成，请勿手动修改
 */
public class CRoleLogin extends Message {

    /**
     * 消息ID
     */
    public static final int ID = 1;

    //角色id
    private int roleId;

    //角色名
    private String roleName = "";

    //角色信息
    private RoleInfo roleInfo = new RoleInfo();

    //角色信息2
    private final List<RoleInfo> roleInfoList = new ArrayList<>();

    //角色信息3
    private final Set<RoleInfo> roleInfoSet = new HashSet<>();

    //角色信息4
    private final Map<Long, RoleInfo> roleInfoMap = new HashMap<>();

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
    public CRoleLogin setRoleId(int roleId) {
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
    public CRoleLogin setRoleName(String roleName) {
        Objects.requireNonNull(roleName,"参数[roleName]不能为空");
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
    public CRoleLogin setRoleInfo(RoleInfo roleInfo) {
        Objects.requireNonNull(roleInfo,"参数[roleInfo]不能为空");
        this.roleInfo = roleInfo;
        return this;
    }

    /**
     * 角色信息2
     */
    public List<RoleInfo> getRoleInfoList() {
        return roleInfoList;
    }

    /**
     * 角色信息3
     */
    public Set<RoleInfo> getRoleInfoSet() {
        return roleInfoSet;
    }

    /**
     * 角色信息4
     */
    public Map<Long, RoleInfo> getRoleInfoMap() {
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
    public CRoleLogin setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    @Override
    public CRoleLogin create() {
        return new CRoleLogin();
    }

    @Override
    public void encode(CodedBuffer buffer) {
        super.encode(buffer);
        
        validate();

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
        for (Long roleInfoMap$Key : this.roleInfoMap.keySet()) {
            buffer.writeLong(roleInfoMap$Key);
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
            Long roleInfoMap$Key = buffer.readLong();
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

        validate();
    }

    @Override
    public void validate() {
        super.validate();

        Objects.requireNonNull(roleName, "字段[roleName]不能为空");
        Objects.requireNonNull(roleInfo, "字段[roleInfo]不能为空");
    }

    @Override
    public String toString() {
        return "CRoleLogin{" +
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
