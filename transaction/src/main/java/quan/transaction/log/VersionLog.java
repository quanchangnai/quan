package quan.transaction.log;

import quan.transaction.MappingData;

/**
 * Created by quanchangnai on 2019/5/17.
 */
public class VersionLog implements Log {

    private long version;

    private MappingData data;

    public VersionLog(MappingData data) {
        this.data = data;
        this.version = data.getVersion();
    }

    public long getVersion() {
        return version;
    }

    public MappingData getData() {
        return data;
    }

    public boolean conflict() {
        return version != data.getVersion();
    }

    @Override
    public void commit() {
        data.versionUp();
    }
}
