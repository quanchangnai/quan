package quan.transaction.log;

import quan.transaction.MappingData;

/**
 * Created by quanchangnai on 2019/5/17.
 */
public class DataLog implements Log {

    private long version;

    private MappingData data;

    public DataLog(MappingData data) {
        this.data = data;
        this.version = data.getVersion();
    }

    public long getVersion() {
        return version;
    }

    public MappingData getData() {
        return data;
    }

    @Override
    public void commit() {
        data.versionUp();
    }
}
