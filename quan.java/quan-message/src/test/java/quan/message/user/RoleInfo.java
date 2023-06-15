package quan.message.user;

import java.util.*;
import quan.message.*;

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
        Objects.requireNonNull(name,"参数[name]不能为空");
        this.name = name;
        return this;
    }

    @Override
    public void encode(CodedBuffer buffer) {
        super.encode(buffer);
        
        validate();

        buffer.writeInt(this.id);
        buffer.writeString(this.name);
    }

    @Override
    public void decode(CodedBuffer buffer) {
        super.decode(buffer);

        this.id = buffer.readInt();
        this.name = buffer.readString();

        validate();
    }

    @Override
    public void validate() {
        super.validate();

        Objects.requireNonNull(name, "字段[name]不能为空");
    }

    @Override
    public String toString() {
        return "RoleInfo{" +
                "id=" + id +
                ",name='" + name + '\'' +
                '}';

    }

}
