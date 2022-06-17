package quan.rpc;

import java.lang.reflect.Array;
import java.util.Objects;

public abstract class Caller {

    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    protected static <T> T[] toArray(Object srcArray, Class<T> componentType) {
        if (srcArray == null) {
            return null;
        }

        Objects.requireNonNull(componentType);
        if (componentType == srcArray.getClass().getComponentType()) {
            return (T[]) srcArray;
        }

        Object[] tempArray = (Object[]) srcArray;
        T[] resultArray = (T[]) Array.newInstance(componentType, tempArray.length);
        System.arraycopy(tempArray, 0, resultArray, 0, tempArray.length);

        return resultArray;
    }

    public abstract Object call(Service service, int methodId, Object... params) throws Exception;

}
