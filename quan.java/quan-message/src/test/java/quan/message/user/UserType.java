package quan.message.user;

/**
 * 用户类型<br/>
 * 代码自动生成，请勿手动修改
 */
public enum UserType {

    /**
     * 用户类型1
     */
    type1(1),

    /**
     * 用户类型2
     */
    type2(2);


    private final int value;

    UserType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static UserType valueOf(int value) {
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
