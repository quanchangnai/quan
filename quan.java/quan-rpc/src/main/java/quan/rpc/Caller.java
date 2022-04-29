package quan.rpc;

import java.lang.reflect.Array;

public interface Caller {

    @SuppressWarnings("unchecked")
    default <T> T[] cast(Object objectArray, Class<T> componentType) {
        if (objectArray == null) {
            return null;
        }

        if (objectArray.getClass().getComponentType() == componentType) {
            return (T[]) objectArray;
        }

        Object[] tempArray = (Object[]) objectArray;
        T[] resultArray = (T[]) Array.newInstance(componentType, tempArray.length);

        for (int i = 0; i < tempArray.length; i++) {
            resultArray[i] = (T) tempArray[i];
        }

        return resultArray;
    }

    Object call(Service service, int methodId, Object... params) throws Exception;

}
