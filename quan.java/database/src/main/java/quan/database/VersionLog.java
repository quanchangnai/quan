package quan.database;

/**
 * 记录数据的版本号
 * Created by quanchangnai on 2019/5/17.
 */
@SuppressWarnings("unchecked")
class VersionLog {

    private long version;

    private Data data;

    public VersionLog(Data data) {
        this.data = data;
        this.version = data._getVersion();
    }

    public Data getData() {
        return data;
    }

    public boolean isConflict() {
        Table table = data.getTable();
        if (table != null) {
            table.checkWorkable();
        }
        return version != data._getVersion();
    }

    public void commit() {
        data.versionUp();
        Table table = data.getTable();
        if (table != null) {
            table.setUpdate(data);
        }
    }
}
