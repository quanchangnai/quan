package quan.definition;

public class DependentSource implements Comparable<DependentSource> {

    private static int nextId;

    private int id = nextId++;

    private Definition definition;

    private DependentType type;

    public DependentSource(Definition definition, DependentType type) {
        this.definition = definition;
        this.type = type;
    }

    public Definition getDefinition() {
        return definition;
    }

    public DependentType getType() {
        return type;
    }

    @Override
    public int compareTo(DependentSource o) {
        return id - o.id;
    }

    public enum DependentType {
        field,//字段依赖
        fieldValue,//集合字段值依赖
        parent,//父类依赖
        child,//子类依赖
        messageHeader//消息头依赖
    }

}
