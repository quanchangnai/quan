package quan.common.tuple;

import java.util.Objects;

/**
 * 三元组
 * Created by quanchangnai on 2019/6/27.
 */
public class Three<T1, T2, T3> extends Two<T1, T2> {

    private T3 three;

    public Three() {
    }

    public Three(T1 one, T2 two, T3 three) {
        super(one, two);
        this.three = three;
    }

    public T3 getThree() {
        return three;
    }

    public Three<T1, T2, T3> setThree(T3 three) {
        this.three = three;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Three<?, ?, ?> that = (Three<?, ?, ?>) o;
        return Objects.equals(three, that.three);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), three);
    }
}
