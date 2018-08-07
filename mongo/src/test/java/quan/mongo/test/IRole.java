package quan.mongo.test;

/**
 * Created by quanchangnai on 2018/8/7.
 */
public interface IRole {

    default void update() {
        System.err.println("IRole update==============");
    }
}
