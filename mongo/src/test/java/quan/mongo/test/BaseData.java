package quan.mongo.test;

import quan.mongo.MappingData;

/**
 * Created by quanchangnai on 2018/8/6.
 */
public class BaseData extends MappingData {

    @Override
    public String collection() {
        return null;
    }

    @Override
    public String[] indexes() {
        return new String[0];
    }
}
