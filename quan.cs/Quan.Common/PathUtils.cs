using System.Collections.Generic;
using System.IO;

namespace Quan.Common
{
    public static class PathUtils
    {
        public static string CurrentPlatPath(string path)
        {
            return path.Replace("/", Path.DirectorySeparatorChar.ToString()).Replace("\\", Path.DirectorySeparatorChar.ToString());
        }

        public static HashSet<FileInfo> ListFiles(DirectoryInfo directory, string extension)
        {
            var childrenFiles = new HashSet<FileInfo>();
            if (!directory.Exists)
            {
                return childrenFiles;
            }

            var files = directory.GetFiles("*." + extension);
            childrenFiles.UnionWith(files);

            foreach (var directoryInfo in directory.GetDirectories())
            {
                childrenFiles.UnionWith(ListFiles(directoryInfo, extension));
            }

            return childrenFiles;
        }
    }
}