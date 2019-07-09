package quan.common.util;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.jar.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by quanchangnai on 2018/12/25.
 */
public class ClassUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * 从字节码里读取类名
     *
     * @param classBytes
     * @return
     */
    public static String readClassName(byte[] classBytes) {
        ClassReader classReader = new ClassReader(classBytes);
        return classReader.getClassName();
    }

    /**
     * 重定义类
     *
     * @param classBytes
     * @throws Exception
     */
    public static synchronized void redefineClass(byte[] classBytes) {
        try {
            String className = readClassName(classBytes);
            Class clazz = Class.forName(className);
            ClassDefinition definition = new ClassDefinition(clazz, classBytes);

            ByteBuddyAgent.install().redefineClasses(definition);
        } catch (Exception e) {
            logger.error("重定义类失败", e);
        }

    }

    /**
     * 开启Lambda热加载支持
     */
    public static void enableLambdaHotAgent() {
        Instrumentation instrumentation = ByteBuddyAgent.install();
        new AgentBuilder.Default()
                .with(AgentBuilder.LambdaInstrumentationStrategy.ENABLED)
                .installOn(instrumentation);
    }


    public static Set<Class<?>> loadClasses(String packageName) {
        return loadClasses(packageName, null, null);
    }

    public static Set<Class<?>> loadClasses(Class<?> superClass) {
        return loadClasses(null, superClass, null);
    }

    /**
     * 加载符合条件的类
     *
     * @param packageName 该包以及子包下面的所有类
     * @param superClass  该类的子孙类
     * @return
     * @throws Exception
     */
    public static Set<Class<?>> loadClasses(String packageName, Class<?> superClass, ClassLoader classLoader) {
        if (packageName == null) {
            packageName = "";
        }
        packageName = packageName.replace(".", "/");

        if (classLoader == null) {
            classLoader = ClassUtils.class.getClassLoader();
        }

        Set<String> classNames = new HashSet<>();
        Set<Class<?>> classes = new HashSet<>();

        Enumeration<URL> urls;
        try {
            urls = classLoader.getResources(packageName);
        } catch (IOException e) {
            logger.error("", e);
            return classes;
        }

        while (urls.hasMoreElements()) {
            try {
                parseUrl(classNames, urls.nextElement(), packageName);
            } catch (Exception e) {
                logger.error("", e);
            }
        }

        for (String className : classNames) {
            Class<?> clazz;
            try {
                clazz = classLoader.loadClass(className);
            } catch (NoClassDefFoundError e) {
                continue;
            } catch (Exception e) {
                logger.error("加载类[{}]失败", className, e);
                continue;
            }
            if (superClass == null || superClass.isAssignableFrom(clazz) && superClass != clazz) {
                classes.add(clazz);
            }
        }

        return classes;

    }

    private static void parseUrl(Set<String> classNames, URL url, String packageName) throws Exception {
        if (url.getProtocol().equals("jar")) {
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            JarFile jarFile = jarURLConnection.getJarFile();
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
        } else if (url.getProtocol().equals("file")) {
            String rootPath = new File(url.toURI()).getCanonicalPath();
            rootPath = rootPath.substring(0, rootPath.lastIndexOf(packageName.replace("/", File.separator)));
            if (!rootPath.endsWith(File.separator)) {
                rootPath += File.separator;
            }

            Set<File> classFiles = new HashSet<>();

            parseFile(classFiles, new File(url.toURI()));

            for (File classFile : classFiles) {
                String filePath = classFile.getCanonicalPath();
                if (filePath.endsWith(".class")) {
                    String className = filePath.substring(rootPath.length(), filePath.lastIndexOf(".class"));
                    className = className.replace(File.separator, ".");
                    classNames.add(className);
                }
            }
        } else {
            logger.error("不支持该url protocol:" + url.getProtocol());
        }
    }

    private static void parseFile(Set<File> resultFiles, File file) throws Exception {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                parseFile(resultFiles, listFile);
            }
        } else {
            String fileName = file.getCanonicalPath();
            if (fileName.endsWith(".class")) {
                resultFiles.add(file);
            }
        }
    }

}
