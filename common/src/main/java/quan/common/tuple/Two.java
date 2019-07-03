package quan.common.tuple;

import java.util.Objects;

/**
 * 二元组
 * Created by quanchangnai on 2019/6/27.
 */
public class Two<V1, V2> extends One<V1> {

    private V2 two;

    public Two() {
        super();
    }

    public Two(V1 one, V2 two) {
        super(one);
        this.two = two;
    }

    public V2 getTwo() {
        return two;
    }

    public Two<V1, V2> setTwo(V2 two) {
        this.two = two;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Two<?, ?> two1 = (Two<?, ?>) o;
        return two.equals(two1.two);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), two);
    }
}
