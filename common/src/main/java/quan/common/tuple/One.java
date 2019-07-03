package quan.common.tuple;

import java.util.Objects;

/**
 * Created by quanchangnai on 2019/7/3.
 */
public class One<V1> {

    private V1 one;

    public One() {
    }

    public One(V1 one) {
        this.one = one;
    }

    public V1 getOne() {
        return one;
    }

    public One<V1> setOne(V1 one) {
        this.one = one;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        One<?> one1 = (One<?>) o;
        return Objects.equals(one, one1.one);
    }

    @Override
    public int hashCode() {
        return Objects.hash(one);
    }
}
