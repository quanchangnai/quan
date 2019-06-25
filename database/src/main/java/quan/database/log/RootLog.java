package quan.database.log;

import quan.database.Data;
import quan.database.Node;

/**
 * Created by quanchangnai on 2019/5/17.
 */
public class RootLog implements Log {

    private Node node;

    private Data root;

    public RootLog(Node node, Data root) {
        this.root = root;
        this.node = node;
    }

    public Data getRoot() {
        return root;
    }

    public RootLog setRoot(Data root) {
        this.root = root;
        return this;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public void commit() {
        node.setRoot(root);
    }

}
