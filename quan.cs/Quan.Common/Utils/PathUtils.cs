using System.Collections.Generic;
using System.IO;

namespace Quan.Common.Utils
{
    public static class PathUtils
    {
        /// <summary>
        /// 把指定路径转换为当前平台路径
        /// </summary>
        /// <param name="path">路径分隔符不明确的路径</param>
        /// <returns></returns>
        public static string ToPlatPath(string path)
        {
            return path.Replace("/", Path.DirectorySeparatorChar.ToString()).Replace("\\", Path.DirectorySeparatorChar.ToString());
        }

        public static HashSet<FileInfo> ListFiles(this DirectoryInfo directory, string extension)
        {
            var childrenFiles = new HashSet<FileInfo>();
            if (!directory.Exists)
            {
                return childrenFiles;
            }

            var files = directory.GetFiles(extension == null ? "*" : "*." + extension);
            childrenFiles.UnionWith(files);

            foreach (var directoryInfo in directory.GetDirectories())
            {
                childrenFiles.UnionWith(directoryInfo.ListFiles(extension));
            }

            return childrenFiles;
        }
    }
}