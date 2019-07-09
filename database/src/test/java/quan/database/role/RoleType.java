package quan.database.role;

/**
 * 角色类型
 * Created by 自动生成
 */
public enum RoleType {

    type1(1),//角色类型1
    type2(2);//角色类型2

    private int value;

    RoleType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RoleType valueOf(int value) {
        switch (value) {
            case 1:
                return type1;
            case 2:
                return type2;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return name();
    }
}
