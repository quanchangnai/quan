package quan.protocol.role;

import java.util.HashSet;
import quan.protocol.user.UserInfo;
import quan.protocol.VarintBuffer;
import java.io.IOException;
import java.util.HashMap;
import quan.protocol.Protocol;
import java.util.ArrayList;

/**
 * 角色登录
 * Created by {@link quan.protocol.generator.JavaGenerator}
 */
public class SRoleLogin extends Protocol {

    public static final int _ID = 2222;//协议id

    @Override
    public int getId() {
        return _ID;
    }

    private long roleId;//角色id
    private RoleInfo roleInfo;//角色信息
    private ArrayList<RoleInfo> roleInfoList;//角色信息
    private HashSet<RoleInfo> roleInfoSet;//角色信息
    private HashMap<Long, RoleInfo> roleInfoMap;//角色信息
    private UserInfo userInfo;//用户信息

    public SRoleLogin() {
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
    public void serialize(VarintBuffer buffer) throws IOException {
        buffer.writeInt(_ID);
        buffer.writeLong(roleId);
        roleInfo.serialize(buffer);
        buffer.writeInt(roleInfoList.size());
        for (RoleInfo roleInfoListValue : roleInfoList) {
            roleInfoListValue.serialize(buffer);
        }
        buffer.writeInt(roleInfoSet.size());
        for (RoleInfo roleInfoSetValue : roleInfoSet) {
            roleInfoSetValue.serialize(buffer);
        }
        buffer.writeInt(roleInfoMap.size());
        for (long roleInfoMapKey : roleInfoMap.keySet()) {
            buffer.writeLong(roleInfoMapKey);
            roleInfoMap.get(roleInfoMapKey).serialize(buffer);
        }
        userInfo.serialize(buffer);
    }

    @Override
    public void parse(VarintBuffer buffer) throws IOException {
        int _id = buffer.readInt();
        if (_id != _ID) {
            throw new IOException("协议ID不匹配,目标值：" + _ID + "，实际值：" + _id);
        }
        roleId = buffer.readLong();
        roleInfo.parse(buffer);
        int roleInfoListSize = buffer.readInt();
        for (int i = 0; i < roleInfoListSize; i++) {
            RoleInfo roleInfoListValue = new RoleInfo();
            roleInfoListValue.parse(buffer);
            roleInfoList.add(roleInfoListValue);
        }
        int roleInfoSetSize = buffer.readInt();
        for (int i = 0; i < roleInfoSetSize; i++) {
            RoleInfo roleInfoSetValue = new RoleInfo();
            roleInfoSetValue.parse(buffer);
            roleInfoSet.add(roleInfoSetValue);
        }
        int roleInfoMapSize = buffer.readInt();
        for (int i = 0; i < roleInfoMapSize; i++) {
            long roleInfoMapKey = buffer.readLong();
            RoleInfo roleInfoMapValue = new RoleInfo();
            roleInfoMapValue.parse(buffer);
            roleInfoMap.put(roleInfoMapKey, roleInfoMapValue);
        }
        userInfo.parse(buffer);
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
