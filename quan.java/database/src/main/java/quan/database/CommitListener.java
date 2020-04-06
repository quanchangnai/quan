package quan.database;

import java.util.Set;

/**
 * Created by quanchangnai on 2020/4/6.
 */
public interface CommitListener {

    void onCommit(Set<Data> changes);

}
