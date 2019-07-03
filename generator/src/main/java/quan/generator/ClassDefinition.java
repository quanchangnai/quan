package quan.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quanchangnai on 2019/6/21.
 */
public abstract class ClassDefinition extends Definition {

    private String packageName;

    /**
     * 类定义所在的文件
     */
    private String fileName;

    private List<FieldDefinition> fields = new ArrayList<>();

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public String getFullName() {
        return packageName + "." + getName();
    }

    public String getFileName() {
        return fileName;
    }

    public ClassDefinition setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name=" + getName() +
                ",packageName=" + getPackageName() +
                ",fields=" + getFields() +
                '}';
    }
}
