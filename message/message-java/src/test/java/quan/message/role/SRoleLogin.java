package quan.message.role;

import quan.message.Buffer;
import java.util.*;
import quan.message.Message;
import quan.message.user.UserInfo;
import java.io.IOException;

/**
 * 角色登录<br/>
 * Created by 自动生成
 */
public class SRoleLogin extends Message {

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

    public SRoleLogin() {
        super(222);
    }

    public long getRoleId() {
        return roleId;
    }

    public SRoleLogin setRoleId(long roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public SRoleLogin setRoleName(String roleName) {
        Objects.requireNonNull(roleName);
        this.roleName = roleName;
        return this;
    }

    public RoleInfo getRoleInfo() {
        return roleInfo;
    }

    public SRoleLogin setRoleInfo(RoleInfo roleInfo) {
        Objects.requireNonNull(roleInfo);
        this.roleInfo = roleInfo;
        return this;
    }

    public ArrayList<RoleInfo> getRoleInfoList() {
        return roleInfoList;
    }

    public HashSet<RoleInfo> getRoleInfoSet() {
        return roleInfoSet;
    }

    public HashMap<Long, RoleInfo> getRoleInfoMap() {
        return roleInfoMap;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public SRoleLogin setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    @Override
    public SRoleLogin create() {
        return new SRoleLogin();
    }

    @Override
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);

        buffer.writeLong(roleId);
        buffer.writeString(roleName);
        roleInfo.encode(buffer);

        buffer.writeInt(roleInfoList.size());
        for (RoleInfo $roleInfoList$Value : roleInfoList) {
            $roleInfoList$Value.encode(buffer);
        }

        buffer.writeInt(roleInfoSet.size());
        for (RoleInfo $roleInfoSet$Value : roleInfoSet) {
            $roleInfoSet$Value.encode(buffer);
        }

        buffer.writeInt(roleInfoMap.size());
        for (long $roleInfoMap$Key : roleInfoMap.keySet()) {
            buffer.writeLong($roleInfoMap$Key);
            roleInfoMap.get($roleInfoMap$Key).encode(buffer);
        }

        buffer.writeBool(userInfo != null);
        if (userInfo != null) {
            userInfo.encode(buffer);
        }

    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

        roleId = buffer.readLong();
        roleName = buffer.readString();
        roleInfo.decode(buffer);

        int $roleInfoList$Size = buffer.readInt();
        for (int i = 0; i < $roleInfoList$Size; i++) {
            RoleInfo $roleInfoList$Value = new RoleInfo();
            $roleInfoList$Value.decode(buffer);
            roleInfoList.add($roleInfoList$Value);
        }

        int $roleInfoSet$Size = buffer.readInt();
        for (int i = 0; i < $roleInfoSet$Size; i++) {
            RoleInfo $roleInfoSet$Value = new RoleInfo();
            $roleInfoSet$Value.decode(buffer);
            roleInfoSet.add($roleInfoSet$Value);
        }

        int $roleInfoMap$Size = buffer.readInt();
        for (int i = 0; i < $roleInfoMap$Size; i++) {
            long $roleInfoMap$Key = buffer.readLong();
            RoleInfo $roleInfoMap$Value = new RoleInfo();
            $roleInfoMap$Value.decode(buffer);
            roleInfoMap.put($roleInfoMap$Key, $roleInfoMap$Value);
        }

        if (buffer.readBool()) {
            if (userInfo == null) {
                userInfo = new UserInfo();
            }
            userInfo.decode(buffer);
        }

    }

    @Override
    public String toString() {
        return "SRoleLogin{" +
                "roleId=" + roleId +
                ",roleName='" + roleName + '\'' +
                ",roleInfo=" + roleInfo +
                ",roleInfoList=" + roleInfoList +
                ",roleInfoSet=" + roleInfoSet +
                ",roleInfoMap=" + roleInfoMap +
                ",userInfo=" + userInfo +
                '}';

    }

}
