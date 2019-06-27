package quan.common.tuple;

import java.util.Objects;

/**
 * 二元组
 * Created by quanchangnai on 2019/6/27.
 */
public class Two<T1, T2> {

    private T1 one;

    private T2 two;

    public Two() {
    }

    public Two(T1 one, T2 two) {
        this.one = one;
        this.two = two;
    }

    public T1 getOne() {
        return one;
    }

    public Two<T1, T2> setOne(T1 one) {
        this.one = one;
        return this;
    }

    public T2 getTwo() {
        return two;
    }

    public Two<T1, T2> setTwo(T2 two) {
        this.two = two;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Two<?, ?> two = (Two<?, ?>) o;
        return Objects.equals(one, two.one) &&
                Objects.equals(this.two, two.two);
    }

    @Override
    public int hashCode() {
        return Objects.hash(one, two);
    }

}
