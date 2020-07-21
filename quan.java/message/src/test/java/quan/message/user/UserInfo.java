package quan.message.user;

import java.util.*;
import java.io.IOException;
import quan.message.*;

/**
 * 自动生成
 */
public class UserInfo extends Bean {

    //ID
    private long id;

    //名字
    private String name = "";

    //等级
    private int level;


    /**
     * ID
     */
    public long getId() {
        return id;
    }

    /**
     * ID
     */
    public UserInfo setId(long id) {
        this.id = id;
        return this;
    }

    /**
     * 名字
     */
    public String getName() {
        return name;
    }

    /**
     * 名字
     */
    public UserInfo setName(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }

    /**
     * 等级
     */
    public int getLevel() {
        return level;
    }

    /**
     * 等级
     */
    public UserInfo setLevel(int level) {
        this.level = level;
        return this;
    }

    @Override
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);

        buffer.writeTag(4);
        buffer.writeLong(this.id);

        buffer.writeTag(11);
        buffer.writeString(this.name);

        buffer.writeTag(12);
        buffer.writeInt(this.level);

        buffer.writeTag(0);
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

        for (int tag = buffer.readTag(); tag != 0; tag = buffer.readTag()) {
            switch (tag) {
                case 4:
                    this.id = buffer.readLong();
                    break;
                case 11:
                    this.name = buffer.readString();
                    break;
                case 12:
                    this.level = buffer.readInt();
                    break;
                default:
                    skipField(tag, buffer);
            }
        }
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",level=" + level +
                '}';

    }

}
