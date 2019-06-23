package quan.common.util;

import java.io.File;
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
public class ClassUtil {

    public static Set<Class<?>> loadClasses(String packageName) throws Exception {

        Set<Class<?>> classes = new HashSet<>();

        packageName = packageName.replace(".", "/");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls = classLoader.getResources(packageName);

        while (urls.hasMoreElements()) {
            parseUrl(classes, urls.nextElement(), packageName);
        }

        return classes;

    }

    private static void parseUrl(Set<Class<?>> classes, URL url, String packageName) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String protocol = url.getProtocol();

        if (protocol.equals("jar")) {
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String jarEntryName = jarEntry.getName();
                if (jarEntryName.endsWith(".class")) {
                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".class"));
                    className = className.replace("/", ".");
                    classes.add(classLoader.loadClass(className));
                }

            }
        }
        if (protocol.equals("file")) {
            String rootPath = new File(url.toURI()).getCanonicalPath();
            rootPath = rootPath.substring(0, rootPath.lastIndexOf(packageName.replace("/", File.separator)));

            Set<File> classFiles = new HashSet<>();

            parseFile(classFiles, new File(url.toURI()));

            for (File classFile : classFiles) {
                String filePath = classFile.getCanonicalPath();
                if (filePath.endsWith(".class")) {
                    String className = filePath.substring(rootPath.length(), filePath.lastIndexOf(".class"));
                    className = className.replace(File.separator, ".");
                    classes.add(classLoader.loadClass(className));
                }
            }

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


    public static void main(String[] args) throws Exception {

    }
}
