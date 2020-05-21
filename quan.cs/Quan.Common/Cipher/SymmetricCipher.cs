using System;
using System.IO;
using System.Security.Cryptography;
using System.Text;

namespace Quan.Common.Cipher
{
    /// <summary>
    /// 对称加密器，支持DES、AES算法
    /// </summary>
    public enum SymmetricAlgorithm
    {
        Des, Aes
    }

    /// <summary>
    /// 对称加密器
    /// </summary>
    public class SymmetricCipher
    {
        public SymmetricAlgorithm Algorithm { get; private set; }

        private ICryptoTransform _encryptor;

        private ICryptoTransform _decryptor;

        private SymmetricCipher()
        {
        }

        public static SymmetricCipher Create(SymmetricAlgorithm algorithm = SymmetricAlgorithm.Des, byte[] secretKey = null)
        {
            var cipher = new SymmetricCipher {Algorithm = algorithm};

            if (algorithm == SymmetricAlgorithm.Des)
            {
                var provider = new DESCryptoServiceProvider();
                secretKey = secretKey ?? provider.Key;
                cipher._encryptor = provider.CreateEncryptor(secretKey, secretKey);
                cipher._decryptor = provider.CreateDecryptor(secretKey, secretKey);
            }
            else
            {
                var provider = new AesCryptoServiceProvider();
                secretKey = secretKey ?? provider.Key;
                cipher._encryptor = provider.CreateEncryptor(secretKey, secretKey);
                cipher._decryptor = provider.CreateDecryptor(secretKey, secretKey);
            }

            return cipher;
        }


        /// <summary>
        /// 加密
        /// </summary>
        /// <param name="data"></param>
        /// <returns></returns>
        public byte[] Encrypt(byte[] data)
        {
            var memoryStream = new MemoryStream();
            var cryptoStream = new CryptoStream(memoryStream, _encryptor, CryptoStreamMode.Write);
            cryptoStream.Write(data, 0, data.Length);
            cryptoStream.FlushFinalBlock();
            return memoryStream.ToArray();
        }

        /// <summary>
        /// 解密
        /// </summary>
        /// <param name="data"></param>
        /// <returns></returns>
        public byte[] Decrypt(byte[] data)
        {
            var memoryStream = new MemoryStream();
            var cryptoStream = new CryptoStream(memoryStream, _decryptor, CryptoStreamMode.Write);
            cryptoStream.Write(data, 0, data.Length);
            cryptoStream.FlushFinalBlock();
            return memoryStream.ToArray();
        }

        public static void Test()
        {
            var cipher = Create();
            var encrypted = cipher.Encrypt(Encoding.UTF8.GetBytes("dadasdaswe"));
            var decrypted = cipher.Decrypt(encrypted);
            Console.WriteLine($"decrypted:${Encoding.UTF8.GetString(decrypted)}");
        }
    }
}