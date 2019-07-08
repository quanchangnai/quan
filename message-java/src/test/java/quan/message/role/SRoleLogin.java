package quan.message.role;

import quan.message.Buffer;
import java.util.*;
import quan.message.Message;
import quan.message.user.UserInfo;
import java.io.IOException;

/**
 * 角色登录
 * Created by 自动生成
 */
public class SRoleLogin extends Message {

    private long roleId = 0L;//角色id

    private String roleName = "";//角色名

    private RoleInfo roleInfo;//角色信息

    private ArrayList<RoleInfo> roleInfoList = new ArrayList<>();//角色信息

    private HashSet<RoleInfo> roleInfoSet = new HashSet<>();//角色信息

    private HashMap<Long, RoleInfo> roleInfoMap = new HashMap<>();//角色信息

    private UserInfo userInfo = new UserInfo();//用户信息

    public SRoleLogin() {
        super(2222);
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
        if (roleName == null){
            throw new NullPointerException();
        }
        this.roleName = roleName;
        return this;
    }

    public RoleInfo getRoleInfo() {
        return roleInfo;
    }

    public SRoleLogin setRoleInfo(RoleInfo roleInfo) {
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
        if (userInfo == null){
            throw new NullPointerException();
        }
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

        buffer.writeBool(roleInfo != null);
        if (roleInfo != null) {
            roleInfo.encode(buffer);
        }

        buffer.writeInt(roleInfoList.size());
        for (RoleInfo roleInfoListValue : roleInfoList) {
            roleInfoListValue.encode(buffer);
        }

        buffer.writeInt(roleInfoSet.size());
        for (RoleInfo roleInfoSetValue : roleInfoSet) {
            roleInfoSetValue.encode(buffer);
        }

        buffer.writeInt(roleInfoMap.size());
        for (long roleInfoMapKey : roleInfoMap.keySet()) {
            buffer.writeLong(roleInfoMapKey);
            roleInfoMap.get(roleInfoMapKey).encode(buffer);
        }

        userInfo.encode(buffer);
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

        roleId = buffer.readLong();
        roleName = buffer.readString();

        if (buffer.readBool()) {
            if (roleInfo == null) {
                roleInfo = new RoleInfo();
            }
            roleInfo.decode(buffer);
        }

        int _roleInfoList_Size = buffer.readInt();
        for (int i = 0; i < _roleInfoList_Size; i++) {
            RoleInfo _roleInfoList_Value = new RoleInfo();
            _roleInfoList_Value.decode(buffer);
            roleInfoList.add(_roleInfoList_Value);
        }

        int _roleInfoSet_Size = buffer.readInt();
        for (int i = 0; i < _roleInfoSet_Size; i++) {
            RoleInfo _roleInfoSet_Value = new RoleInfo();
            _roleInfoSet_Value.decode(buffer);
            roleInfoSet.add(_roleInfoSet_Value);
        }

        int _roleInfoMap_Size = buffer.readInt();
        for (int i = 0; i < _roleInfoMap_Size; i++) {
            long _roleInfoMap_Key = buffer.readLong();
            RoleInfo _roleInfoMap_Value = new RoleInfo();
            _roleInfoMap_Value.decode(buffer);
            roleInfoMap.put(_roleInfoMap_Key, _roleInfoMap_Value);
        }

        userInfo.decode(buffer);
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
