package quan.util;

import aj.org.objectweb.asm.ClassReader;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * Created by quanchangnai on 2018/12/25.
 */
public class ClassUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    private static Instrumentation instrumentation;

    public static Instrumentation getInstrumentation() {
        if (instrumentation == null) {
            instrumentation = ByteBuddyAgent.install();
        }
        return instrumentation;
    }

    private static boolean aopInit;

    /**
     * 初始化AOP
     */
    public synchronized static void initAop() {
        if (!aopInit) {
            aopInit = true;
            getInstrumentation().addTransformer(new ClassPreProcessorAgentAdapter());
            try {
                //环绕通知内联支持
                Class.forName("quan.data.TransactionAspect");
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 从字节码里读取类名
     *
     * @param classBytes 类的字节码
     * @return 类名
     */
    public static String readClassName(byte[] classBytes) {
        Objects.requireNonNull(classBytes, "参数[classBytes]不能为null");
        return new ClassReader(classBytes).getClassName();
    }

    /**
     * 重定义类
     *
     * @param classBytes 一个或多个类的字节码
     */
    public static synchronized void redefineClass(byte[]... classBytes) {
        try {
            ClassDefinition[] classDefinitions = new ClassDefinition[classBytes.length];
            for (int i = 0; i < classBytes.length; i++) {
                String className = readClassName(classBytes[i]);
                Class<?> clazz = Class.forName(className);
                classDefinitions[i] = new ClassDefinition(clazz, classBytes[i]);
            }
            getInstrumentation().redefineClasses(classDefinitions);
        } catch (Exception e) {
            logger.error("重定义类失败", e);
        }
    }

    /**
     * @see #loadClasses(String, Class, String)
     */
    public static Set<Class<?>> loadClasses(String packageName) {
        return loadClasses(packageName, null, null);
    }

    /**
     * @see #loadClasses(String, Class, String)
     */
    public static Set<Class<?>> loadClasses(String packageName, Class<?> superClass) {
        return loadClasses(packageName, superClass, null);
    }

    /**
     * @see #loadClasses(String, Class, String)
     */
    public static Set<Class<?>> loadClasses(String packageName, String classNamePattern) {
        return loadClasses(packageName, null, classNamePattern);
    }


    /**
     * 加载符合条件的类
     *
     * @param packageName      该包以及子包下面的所有类
     * @param superClass       该类的子孙类
     * @param classNamePattern 类名要求的正则格式
     * @return 符合条件的类
     */
    public static Set<Class<?>> loadClasses(String packageName, Class<?> superClass, String classNamePattern) {
        Objects.requireNonNull(packageName, "包名[packageName]不能为空");
        packageName = packageName.replace(".", "/");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtils.class.getClassLoader();
        }


        Enumeration<URL> urls;
        try {
            urls = classLoader.getResources(packageName);
        } catch (IOException e) {
            logger.error("", e);
            return Collections.emptySet();
        }

        Set<String> classNames = new HashSet<>();
        while (urls.hasMoreElements()) {
            try {
                URL url = urls.nextElement();
                if (url.getProtocol().equals("jar")) {
                    parseJar(classNames, ((JarURLConnection) url.openConnection()).getJarFile());
                } else if (url.getProtocol().equals("file")) {
                    parsePath(classNames, new File(url.toURI()), packageName);
                } else {
                    logger.error("不支持该URL协议:" + url.getProtocol());
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }

        Pattern pattern = null;
        if (!StringUtils.isBlank(classNamePattern)) {
            pattern = Pattern.compile(classNamePattern);
        }

        Set<Class<?>> classes = new HashSet<>();
        for (String className : classNames) {
            if (pattern != null && !pattern.matcher(className).matches()) {
                continue;
            }
            Class<?> clazz;
            try {
                clazz = classLoader.loadClass(className);
                clazz.getSimpleName();//继承的类或实现的接口不在classpath下面会报错，忽略该类
            } catch (Throwable e) {
//                logger.error("加载类[{}]失败", className, e);
                continue;
            }
            if (superClass == null || superClass.isAssignableFrom(clazz) && superClass != clazz) {
                classes.add(clazz);
            }
        }

        return classes;
    }

    private static void parseJar(Set<String> classNames, JarFile jarFile) {
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();
            if (jarEntryName.endsWith(".class")) {
                String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".class"));
                className = className.replace("/", ".");
                classNames.add(className);
            }
        }
    }

    private static void parsePath(Set<String> classNames, File path, String packageName) throws Exception {
        String rootPath = path.getCanonicalPath();
        rootPath = rootPath.substring(0, rootPath.lastIndexOf(packageName.replace("/", File.separator)));
        if (!rootPath.endsWith(File.separator)) {
            rootPath += File.separator;
        }

        Set<File> classFiles = CommonUtils.listFiles(path, "class");

        for (File classFile : classFiles) {
            String classFilePath = classFile.getCanonicalPath();
            String className = classFilePath.substring(rootPath.length(), classFilePath.lastIndexOf(".class"));
            className = className.replace(File.separator, ".");
            classNames.add(className);
        }
    }

}
