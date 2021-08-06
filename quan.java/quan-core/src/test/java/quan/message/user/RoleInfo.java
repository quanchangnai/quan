package quan.message.user;

import quan.message.*;
import java.util.*;

/**
 * 角色信息2<br/>
 * 代码自动生成，请勿手动修改
 */
public class RoleInfo extends Bean {

    //角色id
    private int id;

    //角色名
    private String name = "";

    public RoleInfo() {
    }

    public RoleInfo(int id, String name) {
        this.setId(id);
        this.setName(name);
    }

    /**
     * 角色id
     */
    public int getId() {
        return id;
    }

    /**
     * 角色id
     */
    public RoleInfo setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * 角色名
     */
    public String getName() {
        return name;
    }

    /**
     * 角色名
     */
    public RoleInfo setName(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }

    @Override
    public void encode(Buffer buffer) {
        super.encode(buffer);

        buffer.writeInt(this.id);
        buffer.writeString(this.name);
    }

    @Override
    public void decode(Buffer buffer) {
        super.decode(buffer);

        this.id = buffer.readInt();
        this.name = buffer.readString();
    }

    @Override
    public String toString() {
        return "RoleInfo{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }

}
