package quan.message.role;

import quan.message.Buffer;
import java.util.*;
import quan.message.Message;
import java.io.IOException;

/**
 * 角色登录
 * Created by 自动生成
 */
public class CRoleLogin extends Message {

    private long roleId = 0L;

    private String roleName = "";

    public CRoleLogin() {
        super(1111);
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        if (roleName == null){
            throw new NullPointerException();
        }
        this.roleName = roleName;
    }

    @Override
    public CRoleLogin create() {
        return new CRoleLogin();
    }

    @Override
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);

        buffer.writeLong(roleId);
        buffer.writeString(roleName);
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

        roleId = buffer.readLong();
        roleName = buffer.readString();
    }

    @Override
    public String toString() {
        return "CRoleLogin{" +
                "roleId=" + roleId +
                ",roleName='" + roleName + '\'' +
                '}';

    }

}
