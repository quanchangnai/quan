package quan.message.role;

import java.util.*;
import quan.message.*;
import quan.util.NumberUtils;

/**
 * 角色信息<br/>
 * 代码自动生成，请勿手动修改
 */
public class RoleInfo extends Bean {

    //角色id
    private int id;

    //角色名
    private String name = "";

    private String alias;

    private RoleType type;

    private boolean b;

    private short s;

    private int i;

    private double d;

    private byte[] bb1 = new byte[0];

    private byte[] bb2;

    private final List<Integer> list = new ArrayList<>();

    private final Set<Integer> set = new HashSet<>();

    private final Map<Integer, Integer> map = new HashMap<>();


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

    public String getAlias() {
        return alias;
    }

    public RoleInfo setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public RoleType getType() {
        return type;
    }

    public RoleInfo setType(RoleType type) {
        this.type = type;
        return this;
    }

    public boolean getB() {
        return b;
    }

    public RoleInfo setB(boolean b) {
        this.b = b;
        return this;
    }

    public short getS() {
        return s;
    }

    public RoleInfo setS(short s) {
        this.s = s;
        return this;
    }

    public int getI() {
        return i;
    }

    public RoleInfo setI(int i) {
        NumberUtils.validateRange(i, 1, 20, "参数[i]");
        this.i = i;
        return this;
    }

    public double getD() {
        return d;
    }

    public RoleInfo setD(double d) {
        this.d = d;
        return this;
    }

    public byte[] getBb1() {
        return bb1;
    }

    public RoleInfo setBb1(byte[] bb1) {
        Objects.requireNonNull(bb1,"参数[bb1]不能为空");
        this.bb1 = bb1;
        return this;
    }

    public byte[] getBb2() {
        return bb2;
    }

    public RoleInfo setBb2(byte[] bb2) {
        this.bb2 = bb2;
        return this;
    }

    public List<Integer> getList() {
        return list;
    }

    public Set<Integer> getSet() {
        return set;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    @Override
    public void encode(CodedBuffer buffer) {
        super.encode(buffer);
        
        validate();

        buffer.writeInt(this.id);
        buffer.writeString(this.name);

        buffer.writeBool(this.alias != null);
        if (this.alias != null) {
            buffer.writeString(this.alias);
        }

        buffer.writeInt(this.type == null ? 0 : this.type.value);
        buffer.writeBool(this.b);
        buffer.writeShort(this.s);
        buffer.writeInt(this.i);
        buffer.writeDouble(this.d);
        buffer.writeBytes(this.bb1);

        buffer.writeBool(this.bb2 != null);
        if (this.bb2 != null) {
            buffer.writeBytes(this.bb2);
        }

        buffer.writeInt(this.list.size());
        for (Integer list$Value : this.list) {
            buffer.writeInt(list$Value);
        }

        buffer.writeInt(this.set.size());
        for (Integer set$Value : this.set) {
            buffer.writeInt(set$Value);
        }
    }

    @Override
    public void decode(CodedBuffer buffer) {
        super.decode(buffer);

        this.id = buffer.readInt();
        this.name = buffer.readString();

        if (buffer.readBool()) {
           this.alias = buffer.readString();
        }

        this.type = RoleType.valueOf(buffer.readInt());
        this.b = buffer.readBool();
        this.s = buffer.readShort();
        this.i = buffer.readInt();
        this.d = buffer.readDouble();
        this.bb1 = buffer.readBytes();

        if (buffer.readBool()) {
           this.bb2 = buffer.readBytes();
        }

        int list$Size = buffer.readInt();
        for (int i = 0; i < list$Size; i++) {
            this.list.add(buffer.readInt());
        }

        int set$Size = buffer.readInt();
        for (int i = 0; i < set$Size; i++) {
            this.set.add(buffer.readInt());
        }

        validate();
    }

    @Override
    public void validate() {
        super.validate();

        Objects.requireNonNull(name, "字段[name]不能为空");
        NumberUtils.validateRange(i, 1, 20, "字段[i]");
        Objects.requireNonNull(bb1, "字段[bb1]不能为空");
    }

    @Override
    public String toString() {
        return "RoleInfo{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",alias='" + alias + '\'' +
                ",type=" + type +
                ",b=" + b +
                ",s=" + s +
                ",i=" + i +
                ",d=" + d +
                ",bb1=" + Arrays.toString(bb1) +
                ",bb2=" + Arrays.toString(bb2) +
                ",list=" + list +
                ",set=" + set +
                ",map=" + map +
                '}';

    }

}
