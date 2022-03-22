package quan.definition;

public class DependentSource implements Comparable<DependentSource> {

    private static int nextId;

    private int id = nextId++;

    private DependentType type;

    private ClassDefinition ownerClass;

    private Definition ownerDefinition;

    private ClassDefinition dependentClass;

    public DependentSource(DependentType type, ClassDefinition ownerClass, Definition ownerDefinition, ClassDefinition dependentClass) {
        this.type = type;
        this.ownerClass = ownerClass;
        this.ownerDefinition = ownerDefinition;
        this.dependentClass = dependentClass;
    }

    public Definition getOwnerDefinition() {
        return ownerDefinition;
    }

    public DependentType getType() {
        return type;
    }

    public ClassDefinition getDependentClass() {
        return dependentClass;
    }

    public DependentSource setDependentClass(ClassDefinition dependentClass) {
        this.dependentClass = dependentClass;
        return this;
    }

    @Override
    public int compareTo(DependentSource other) {
        if (dependentClass != other.dependentClass) {
            if (ownerClass.getPackageName().equals(dependentClass.getPackageName())) {
                return -1;
            }
            if (ownerClass.getPackageName().equals(other.dependentClass.getPackageName())) {
                return 1;
            }
        }
        return id - other.id;
    }

    public enum DependentType {
        FIELD,//字段依赖
        FIELD_VALUE,//集合字段值依赖
        FIELD_REF,//字段引用依赖
        PARENT,//父类依赖
        CHILD,//子类依赖
    }

}
