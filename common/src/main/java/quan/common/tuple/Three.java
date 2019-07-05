package quan.common.tuple;

import java.util.Objects;

/**
 * 三元组
 * Created by quanchangnai on 2019/6/27.
 */
public class Three<V1, V2, V3> extends Two<V1, V2> {

    private V3 three;

    public Three() {
    }

    public Three(V1 one, V2 two, V3 three) {
        super(one, two);
        this.three = three;
    }

    public V3 getThree() {
        return three;
    }

    public Three<V1, V2, V3> setThree(V3 three) {
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

    @Override
    public String toString() {
        return "Three{" +
                "one=" + getOne() +
                "two=" + getTwo() +
                "three=" + three +
                '}';
    }
}
