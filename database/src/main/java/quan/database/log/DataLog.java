package quan.database.log;

import quan.database.Data;

/**
 * Created by quanchangnai on 2019/5/17.
 */
public class DataLog implements Log {

    private long version;

    private Data data;

    public DataLog(Data data) {
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
        return version != data.getVersion();
    }

    @Override
    public void commit() {
        data.versionUp();
    }
}
