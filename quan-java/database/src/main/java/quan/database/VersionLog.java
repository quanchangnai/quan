package quan.database;

/**
 * Created by quanchangnai on 2019/5/17.
 */
class VersionLog {

    private long version;

    private Data data;

    public VersionLog(Data data) {
        this.data = data;
        this.version = data.getVersion();
    }

    public long getVersion() {
        return version;
    }

    public Data getData() {
        return data;
    }

    public boolean isConflict() {
        Cache cache = data.getCache();
        if (cache != null) {
            cache.checkWorkable();
        }
        return version != data.getVersion();
    }

    public void commit() {
        data.versionUp();
        Cache cache = data.getCache();
        if (cache != null) {
            cache.setUpdate(data);
        }
    }
}
