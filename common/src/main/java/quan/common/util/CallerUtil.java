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

    private static void validCallerClass(Collection<Class> allowClasses, int calleeIndex) {
        if (!valid) {
            return;
        }

        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        if (stackTrace.length < calleeIndex + 2) {
            return;
        }

        Class<?> calleeClass;
        Class<?> callerClass;
        try {
            calleeClass = Class.forName(stackTrace[calleeIndex].getClassName());
            callerClass = Class.forName(stackTrace[calleeIndex + 1].getClassName());
        } catch (ClassNotFoundException e) {
            return;
        }

        if (calleeClass.isAssignableFrom(callerClass)) {
            return;
        }

        boolean allowCall = false;
        for (Class allowClass : allowClasses) {
            if (allowClass.isAssignableFrom(callerClass)) {
                allowCall = true;
            }
        }

        if (!allowCall) {
            String calleeMethodName = stackTrace[calleeIndex].getMethodName();
            throw new UnsupportedOperationException("方法[" + calleeClass.getName() + "." + calleeMethodName + "()]不允许被类[" + callerClass.getName() + "]调用");
        }
    }


    private static void validCallerPackage(Collection<Package> allowPackages, int calleeIndex) {
        if (!valid) {
            return;
        }

        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        if (stackTrace.length < calleeIndex + 2) {
            return;
        }

        String calleeClassName = stackTrace[calleeIndex].getClassName();
        String callerClassName = stackTrace[calleeIndex + 1].getClassName();

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
            String calleeMethodName = stackTrace[calleeIndex].getMethodName();
            throw new UnsupportedOperationException("方法[" + calleeClassName + "." + calleeMethodName + "()]不允许被类[" + callerClassName + "]调用");
        }
    }

    public static void validCallerClass(Collection<Class> allowClasses) {
        validCallerClass(allowClasses, 2);
    }

    public static void validCallerClass(Class allowClass) {
        validCallerClass(Arrays.asList(allowClass), 2);
    }

    public static void validCallerPackage(Collection<Package> allowPackages) {
        validCallerPackage(allowPackages, 2);
    }

    public static void validCallerPackage(Package allowPackage) {
        validCallerPackage(Arrays.asList(allowPackage), 2);
    }

}
