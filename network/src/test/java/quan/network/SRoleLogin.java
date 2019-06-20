package quan.network;

import quan.network.message.Buffer;
import quan.network.message.Message;

import java.io.IOException;


public class SRoleLogin extends Message {

    @Override
    public SRoleLogin create() {
        return new SRoleLogin();
    }

    private long roleId;//角色ID

    private String roleName;//角色名字

    private long loginTime;//登陆时间

    public SRoleLogin() {
        super(111);
        roleId = 0L;
        roleName = "";
        loginTime = System.nanoTime();
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
        this.roleName = roleName;
    }

    @Override
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);
        buffer.writeLong(roleId);
        buffer.writeString(roleName);
        buffer.writeLong(loginTime);
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);
        roleId = buffer.readLong();
        roleName = buffer.readString();
        loginTime = buffer.readLong();
    }

    @Override
    public String toString() {
        return "SRoleLogin{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", loginTime=" + loginTime +
                '}';
    }
}
