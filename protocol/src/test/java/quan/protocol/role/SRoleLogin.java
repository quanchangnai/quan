package quan.protocol.role;

import java.util.HashSet;
import quan.protocol.user.UserInfo;
import quan.protocol.VarIntBuffer;
import java.io.IOException;
import java.util.HashMap;
import quan.protocol.Protocol;
import java.util.ArrayList;

/**
 * 角色登录
 * Created by {@link quan.protocol.generator.JavaGenerator}
 */
public class SRoleLogin extends Protocol {

    public static final int ID = 2222;//协议id

    @Override
    public int getId() {
        return ID;
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
    public void serialize(VarIntBuffer buffer) throws IOException {
        buffer.writeInt(ID);
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
    public void parse(VarIntBuffer buffer) throws IOException {
        if (buffer.readInt() != ID) {
            buffer.reset();
            throw new IOException("协议解析出错，id不匹配,目标值：" + ID + "，实际值：" + buffer.readInt());
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
