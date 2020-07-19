package quan.message.role;

import java.util.*;
import java.io.IOException;
import quan.message.*;
import quan.message.user.UserInfo;
import quan.message.common.MessageHeader;

/**
 * 角色登录，自定义ID<br/>
 * 自动生成
 */
public class CRoleLogin extends MessageHeader {

    /**
     * 消息ID
     */
    public static final int ID = 1;

    //角色id
    private long roleId;

    //角色名
    private String roleName = "";

    //角色信息
    private RoleInfo roleInfo = new RoleInfo();

    //角色信息
    private ArrayList<RoleInfo> roleInfoList = new ArrayList<>();

    //角色信息
    private HashSet<RoleInfo> roleInfoSet = new HashSet<>();

    //角色信息
    private HashMap<Long, RoleInfo> roleInfoMap = new HashMap<>();

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
     * 消息序号
     */
    @Override
    public CRoleLogin setSeq(long seq) {
        super.setSeq(seq);
        return this;
    }

    /**
     * 错误码
     */
    @Override
    public CRoleLogin setError(int error) {
        super.setError(error);
        return this;
    }

    /**
     * 角色id
     */
    public long getRoleId() {
        return roleId;
    }

    /**
     * 角色id
     */
    public CRoleLogin setRoleId(long roleId) {
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
    public CRoleLogin setRoleInfo(RoleInfo roleInfo) {
        Objects.requireNonNull(roleInfo);
        this.roleInfo = roleInfo;
        return this;
    }

    /**
     * 角色信息
     */
    public ArrayList<RoleInfo> getRoleInfoList() {
        return roleInfoList;
    }

    /**
     * 角色信息
     */
    public HashSet<RoleInfo> getRoleInfoSet() {
        return roleInfoSet;
    }

    /**
     * 角色信息
     */
    public HashMap<Long, RoleInfo> getRoleInfoMap() {
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
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);

        buffer.writeLong(this.roleId);
        buffer.writeString(this.roleName);
        this.roleInfo.encode(buffer);

        buffer.writeInt(this.roleInfoList.size());
        for (RoleInfo $roleInfoList$Value : this.roleInfoList) {
            $roleInfoList$Value.encode(buffer);
        }

        buffer.writeInt(this.roleInfoSet.size());
        for (RoleInfo $roleInfoSet$Value : this.roleInfoSet) {
            $roleInfoSet$Value.encode(buffer);
        }

        buffer.writeInt(this.roleInfoMap.size());
        for (Long $roleInfoMap$Key : this.roleInfoMap.keySet()) {
            buffer.writeLong($roleInfoMap$Key);
            this.roleInfoMap.get($roleInfoMap$Key).encode(buffer);
        }

        buffer.writeBool(this.userInfo != null);
        if (this.userInfo != null) {
            this.userInfo.encode(buffer);
        }
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

        this.roleId = buffer.readLong();
        this.roleName = buffer.readString();
        this.roleInfo.decode(buffer);

        int $roleInfoList$Size = buffer.readInt();
        for (int i = 0; i < $roleInfoList$Size; i++) {
            RoleInfo $roleInfoList$Value = new RoleInfo();
            $roleInfoList$Value.decode(buffer);
            this.roleInfoList.add($roleInfoList$Value);
        }

        int $roleInfoSet$Size = buffer.readInt();
        for (int i = 0; i < $roleInfoSet$Size; i++) {
            RoleInfo $roleInfoSet$Value = new RoleInfo();
            $roleInfoSet$Value.decode(buffer);
            this.roleInfoSet.add($roleInfoSet$Value);
        }

        int $roleInfoMap$Size = buffer.readInt();
        for (int i = 0; i < $roleInfoMap$Size; i++) {
            Long $roleInfoMap$Key = buffer.readLong();
            RoleInfo $roleInfoMap$Value = new RoleInfo();
            $roleInfoMap$Value.decode(buffer);
            this.roleInfoMap.put($roleInfoMap$Key, $roleInfoMap$Value);
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
        return "CRoleLogin{" +
                "seq=" + seq +
                ",error=" + error +
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
