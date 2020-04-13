package quan.database;

import java.util.List;

/**
 * Created by quanchangnai on 2020/4/12.
 */
public class Index {

    private List<String> fields;

    private boolean unique;

    public Index(List<String> fields, boolean unique) {
        this.fields = fields;
        this.unique = unique;
    }

    public List<String> getFields() {
        return fields;
    }

    public boolean isUnique() {
        return unique;
    }

}
