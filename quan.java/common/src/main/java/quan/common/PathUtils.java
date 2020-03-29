package quan.common;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by quanchangnai on 2019/8/4.
 */
public class PathUtils {

    /**
     * 把指定路径转换为当前平台路径
     *
     * @param path 路径风格符不明确的路径
     * @return 转换后的路径
     */
    public static String toPlatPath(String path) {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        return path.replace("/", File.separator).replace("\\", File.separator);
    }

    /**
     * 递归列出子文件
     *
     * @param path      目录
     * @param extension 扩展名
     * @return 子文件集合
     */
    public static Set<File> listFiles(File path, String extension) {
        Set<File> childrenFiles = new HashSet<>();
        listFiles(path, extension, childrenFiles);
        return childrenFiles;
    }

    private static void listFiles(File path, String extension, Set<File> childrenFiles) {
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    listFiles(file, extension, childrenFiles);
                }
            }
        } else if (path.getName().endsWith("." + extension)) {
            childrenFiles.add(path);
        }
    }
}
