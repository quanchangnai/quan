package quan.common.util;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by quanchangnai on 2019/6/26.
 */
public class CallerUtil {

    private static boolean valid = true;

    public static void setValid(boolean valid) {
        CallerUtil.valid = valid;
    }

    public static Class getCallerClass() {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        if (stackTrace.length <= 2) {
            return null;
        }

        try {
            return Class.forName(stackTrace[2].getClassName());
        } catch (ClassNotFoundException e) {
            return null;
        }

    }

    private static void validCallerClass(Collection<Class> allowClasses, int startDepth) {
        if (!valid) {
            return;
        }

        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        if (stackTrace.length <= startDepth + 2) {
            return;
        }

        String calleeClassName = stackTrace[startDepth + 1].getClassName();
        String calleeMethodName = stackTrace[startDepth + 1].getMethodName();
        String callerClassName = stackTrace[startDepth + 2].getClassName();

        if (calleeClassName.equals(callerClassName)) {
            return;
        }

        boolean allowCall = false;
        for (Class allowClass : allowClasses) {
            if (allowClass.getName().equals(callerClassName)) {
                allowCall = true;
            }
        }

        if (!allowCall) {
            throw new UnsupportedOperationException("方法[" + calleeClassName + "." + calleeMethodName + "()]不允许被类[" + callerClassName + "]调用");
        }
    }


    private static void validCallerPackage(Collection<Package> allowPackages, int startDepth) {
        if (!valid) {
            return;
        }

        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        if (stackTrace.length <= startDepth + 2) {
            return;
        }

        String calleeClassName = stackTrace[startDepth + 1].getClassName();
        String calleeMethodName = stackTrace[startDepth + 1].getMethodName();
        String callerClassName = stackTrace[startDepth + 2].getClassName();

        Class<?> calleeClass;
        Class<?> callerClass;
        try {
            calleeClass = Class.forName(calleeClassName);
            callerClass = Class.forName(callerClassName);
        } catch (ClassNotFoundException e) {
            return;
        }

        if (calleeClass.getPackage().equals(callerClass.getPackage())) {
            return;
        }

        boolean allowCall = false;
        for (Package allowPackage : allowPackages) {
            if (allowPackage.equals(callerClass.getPackage())) {
                allowCall = true;
            }
        }

        if (!allowCall) {
            throw new UnsupportedOperationException("方法[" + calleeClassName + "." + calleeMethodName + "()]不允许被类[" + callerClassName + "]调用");
        }
    }

    public static void validCallerClass(Collection<Class> allowClasses) {
        validCallerClass(allowClasses, 1);
    }

    public static void validCallerClass(Class allowClass) {
        validCallerClass(Arrays.asList(allowClass), 1);
    }

    public static void validCallerPackage(Collection<Package> allowPackages) {
        validCallerPackage(allowPackages, 1);
    }

    public static void validCallerPackage(Package allowPackage) {
        validCallerPackage(Arrays.asList(allowPackage), 1);
    }

}
