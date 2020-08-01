package quan.message.user;

import java.util.*;
import quan.message.*;

/**
 * 角色信息2<br/>
 * 自动生成，请勿修改
 */
public class RoleInfo extends Bean {

    //角色id
    private long id;

    //角色名
    private String name = "";


    /**
     * 角色id
     */
    public long getId() {
        return id;
    }

    /**
     * 角色id
     */
    public RoleInfo setId(long id) {
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

        buffer.writeLong(this.id);
        buffer.writeString(this.name);
    }

    @Override
    public void decode(Buffer buffer) {
        super.decode(buffer);

        this.id = buffer.readLong();
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
