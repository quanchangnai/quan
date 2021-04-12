package quan.definition;

public enum Category {

    data("数据"), message("消息"), config("配置");

    private final String alias;

    Category(String alias) {
        this.alias = alias;
    }

    public String alias() {
        return alias;
    }
    
}
