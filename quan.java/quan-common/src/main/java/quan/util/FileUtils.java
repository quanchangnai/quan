package quan.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件工具
 */
public class FileUtils {

    /**
     * 转换为当前平台文件路径
     *
     * @param path 路径分隔符不明确的路径
     * @return 转换后的路径
     */
    public static String toPlatPath(String path) {
        if (StringUtils.isBlank(path)) {
            return path;
        }
        return path.replace("/", File.separator).replace("\\", File.separator);
    }

    /**
     * 递归列出子文件
     *
     * @param path 目录
     * @param ext  扩展名，为空时不限制
     * @return 子文件集合
     */
    public static Set<File> listFiles(File path, String ext) {
        Set<File> children = new HashSet<>();
        listFiles(path, ext, children);
        return children;
    }

    /**
     * 递归列出子文件
     *
     * @see #listFiles(File, String)
     */
    public static Set<File> listFiles(File path) {
        return listFiles(path, null);
    }

    private static void listFiles(File path, String ext, Set<File> children) {
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    listFiles(file, ext, children);
                }
            }
        } else if (ext == null || path.getName().endsWith("." + ext)) {
            children.add(path);
        }
    }

}
