package quan.data.role;

/**
 * 角色类型<br/>
 * 自动生成
 */
public enum RoleType {

    /**
     * 角色类型1
     */
    type1(1),

    /**
     * 角色类型2
     */
    type2(2);


    private int value;

    RoleType(int value) {
        this.value = value;
    }

    public int value() {
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

}
