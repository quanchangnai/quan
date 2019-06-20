package quan.generator.role;

import java.util.HashSet;
import quan.generator.user.UserInfo;
import quan.network.message.Buffer;
import java.io.IOException;
import java.util.HashMap;
import quan.network.message.Message;
import java.util.ArrayList;

/**
 * 角色登录
 * Created by 自动生成
 */
public class SRoleLogin extends Message {

    private long roleId;//角色id
    private RoleInfo roleInfo;//角色信息
    private ArrayList<RoleInfo> roleInfoList;//角色信息
    private HashSet<RoleInfo> roleInfoSet;//角色信息
    private HashMap<Long, RoleInfo> roleInfoMap;//角色信息
    private UserInfo userInfo;//用户信息

    public SRoleLogin() {
        super(2222);
        roleId = 111L;
        roleInfo = new RoleInfo();
        roleInfoList = new ArrayList<>();
        roleInfoSet = new HashSet<>();
        roleInfoMap = new HashMap<>();
        userInfo = new UserInfo();
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public RoleInfo getRoleInfo() {
        return roleInfo;
    }

    public void setRoleInfo(RoleInfo roleInfo) {
        this.roleInfo = roleInfo;
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

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }


    @Override
    public SRoleLogin create() {
        return new SRoleLogin();
    }

    @Override
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);
        buffer.writeLong(roleId);
        roleInfo.encode(buffer);
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
        super.encode(buffer);
        roleId = buffer.readLong();
        roleInfo.decode(buffer);
        int roleInfoListSize = buffer.readInt();
        for (int i = 0; i < roleInfoListSize; i++) {
            RoleInfo roleInfoListValue = new RoleInfo();
            roleInfoListValue.decode(buffer);
            roleInfoList.add(roleInfoListValue);
        }
        int roleInfoSetSize = buffer.readInt();
        for (int i = 0; i < roleInfoSetSize; i++) {
            RoleInfo roleInfoSetValue = new RoleInfo();
            roleInfoSetValue.decode(buffer);
            roleInfoSet.add(roleInfoSetValue);
        }
        int roleInfoMapSize = buffer.readInt();
        for (int i = 0; i < roleInfoMapSize; i++) {
            long roleInfoMapKey = buffer.readLong();
            RoleInfo roleInfoMapValue = new RoleInfo();
            roleInfoMapValue.decode(buffer);
            roleInfoMap.put(roleInfoMapKey, roleInfoMapValue);
        }
        userInfo.decode(buffer);
    }

    @Override
    public String toString() {
        return "SRoleLogin{" +
                "roleId=" + roleId +
                ",roleInfo=" + roleInfo +
                ",roleInfoList=" + roleInfoList +
                ",roleInfoSet=" + roleInfoSet +
                ",roleInfoMap=" + roleInfoMap +
                ",userInfo=" + userInfo +
                '}';

    }

}
