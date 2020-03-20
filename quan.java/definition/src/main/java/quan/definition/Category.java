package quan.definition;

public enum Category {

    data("数据"), message("消息"), config("配置");

    private final String comment;

    Category(String comment) {
        this.comment = comment;
    }

    public String comment() {
        return comment;
    }
}
