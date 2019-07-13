package quan.message.user;

import quan.message.Buffer;
import java.util.*;
import java.io.IOException;
import quan.message.Bean;

/**
 * Created by 自动生成
 */
public class UserInfo extends Bean {

    //ID
    private long id;

    //名字
    private String name = "";

    //等级
    private int level;

    public UserInfo() {
    }

    public long getId() {
        return id;
    }

    public UserInfo setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserInfo setName(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }

    public int getLevel() {
        return level;
    }

    public UserInfo setLevel(int level) {
        this.level = level;
        return this;
    }

    @Override
    public void encode(Buffer buffer) throws IOException {
        super.encode(buffer);

        buffer.writeLong(id);
        buffer.writeString(name);
        buffer.writeInt(level);
    }

    @Override
    public void decode(Buffer buffer) throws IOException {
        super.decode(buffer);

        id = buffer.readLong();
        name = buffer.readString();
        level = buffer.readInt();
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
