package quan.util;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by quanchangnai on 2019/6/26.
 */
public class MethodUtils {

    private static boolean validateCaller = true;

    public static void setValidateCaller(boolean validateCaller) {
        MethodUtils.validateCaller = validateCaller;
    }

    public static Class<?> getCallerClass() {
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

    private static void doValidateCallerClass(Collection<Class<?>> allowClasses) {
        if (!validateCaller) {
            return;
        }

        int calleeIndex = 2;

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
        for (Class<?> allowClass : allowClasses) {
            if (allowClass.isAssignableFrom(callerClass)) {
                allowCall = true;
            }
        }

        if (!allowCall) {
            String calleeMethodName = stackTrace[calleeIndex].getMethodName();
            throw new UnsupportedOperationException("方法[" + calleeClass.getName() + "." + calleeMethodName + "()]不允许被类[" + callerClass.getName() + "]调用");
        }
    }

    private static void doValidateCallerPackage(Collection<Package> allowPackages) {
        if (!validateCaller) {
            return;
        }

        int calleeIndex = 2;

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
                break;
            }
        }

        if (!allowCall) {
            String calleeMethodName = stackTrace[calleeIndex].getMethodName();
            throw new UnsupportedOperationException("方法[" + calleeClassName + "." + calleeMethodName + "()]不允许被类[" + callerClassName + "]调用");
        }
    }

    public static void validateCallerClass(Collection<Class<?>> allowClasses) {
        doValidateCallerClass(allowClasses);
    }

    public static void validateCallerClass(Class<?>... allowClass) {
        doValidateCallerClass(Arrays.asList(allowClass));
    }

    public static void validateCallerPackage(Collection<Package> allowPackages) {
        doValidateCallerPackage(allowPackages);
    }

    public static void validateCallerPackage(Package... allowPackage) {
        doValidateCallerPackage(Arrays.asList(allowPackage));
    }

}
