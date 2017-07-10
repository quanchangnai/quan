package quan.protocol.role;

import java.util.HashSet;
import quan.protocol.user.UserInfo;
import java.io.IOException;
import quan.protocol.stream.WritableStream;
import java.util.HashMap;
import quan.protocol.Protocol;
import quan.protocol.stream.ReadableStream;
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
    public void serialize(WritableStream writable) throws IOException {
        writable.writeInt(ID);
        writable.writeLong(roleId);
        roleInfo.serialize(writable);
        writable.writeInt(roleInfoList.size());
        for (RoleInfo roleInfoListValue : roleInfoList) {
            roleInfoListValue.serialize(writable);
        }
        writable.writeInt(roleInfoSet.size());
        for (RoleInfo roleInfoSetValue : roleInfoSet) {
            roleInfoSetValue.serialize(writable);
        }
        writable.writeInt(roleInfoMap.size());
        for (long roleInfoMapKey : roleInfoMap.keySet()) {
            writable.writeLong(roleInfoMapKey);
            roleInfoMap.get(roleInfoMapKey).serialize(writable);
        }
        userInfo.serialize(writable);
    }

    @Override
    public void parse(ReadableStream readable) throws IOException {
        if (readable.readInt() != ID) {
            readable.reset();
            throw new IOException("协议解析出错，id不匹配,目标值：" + ID + "，实际值：" + readable.readInt());
        }
        roleId = readable.readLong();
        roleInfo.parse(readable);
        int roleInfoListSize = readable.readInt();
        for (int i = 0; i < roleInfoListSize; i++) {
            RoleInfo roleInfoListValue = new RoleInfo();
            roleInfoListValue.parse(readable);
            roleInfoList.add(roleInfoListValue);
        }
        int roleInfoSetSize = readable.readInt();
        for (int i = 0; i < roleInfoSetSize; i++) {
            RoleInfo roleInfoSetValue = new RoleInfo();
            roleInfoSetValue.parse(readable);
            roleInfoSet.add(roleInfoSetValue);
        }
        int roleInfoMapSize = readable.readInt();
        for (int i = 0; i < roleInfoMapSize; i++) {
            long roleInfoMapKey = readable.readLong();
            RoleInfo roleInfoMapValue = new RoleInfo();
            roleInfoMapValue.parse(readable);
            roleInfoMap.put(roleInfoMapKey, roleInfoMapValue);
        }
        userInfo.parse(readable);
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
