package quan.database.log;

import quan.database.Data;
import quan.database.Node;

/**
 * 记录节点的根
 * Created by quanchangnai on 2019/5/17.
 */
public class RootLog extends Node.Setter {

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

    public void commit() {
        _setRoot(node, root);
    }

}
