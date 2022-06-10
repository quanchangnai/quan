package quan.rpc;

import java.lang.reflect.Array;
import java.util.Objects;

public interface Caller {

    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    default <T> T[] cast(Object srcArray, Class<T> componentType) {
        Objects.requireNonNull(componentType);
        if (srcArray == null) {
            return null;
        }

        if (componentType == srcArray.getClass().getComponentType()) {
            return (T[]) srcArray;
        }

        Object[] tempArray = (Object[]) srcArray;
        T[] resultArray = (T[]) Array.newInstance(componentType, tempArray.length);
        System.arraycopy(tempArray, 0, resultArray, 0, tempArray.length);

        return resultArray;
    }

    Object call(Service service, int methodId, Object... params) throws Exception;

}
