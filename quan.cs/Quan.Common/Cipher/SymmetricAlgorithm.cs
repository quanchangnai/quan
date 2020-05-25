using System.Text;

namespace Quan.Common.Cipher
{
    public class SymmetricAlgorithm
    {
        //Data Encryption Standard
        public static readonly SymmetricAlgorithm Des = new SymmetricAlgorithm("DES", "DES/CBC/PKCS5Padding", "12345678", 56);

        //三重DES
        public static readonly SymmetricAlgorithm DesEde = new SymmetricAlgorithm("DESEde", "DESEde/CBC/PKCS5Padding", "12345678", 168);

        //Advanced Encryption Standard
        public static readonly SymmetricAlgorithm Aes = new SymmetricAlgorithm("AES", "AES/CBC/PKCS5Padding", "1234567812345678", 128);

        //密钥生成算法
        public readonly string Generation;

        //加密、解密算法
        public readonly string Encryption;

        //默认初始向量
        public readonly byte[] Iv;

        //密钥大小
        public readonly int KeySize;

        public SymmetricAlgorithm(string generation, string encryption, string iv, int keySize)
        {
            Generation = generation;
            Encryption = encryption;
            KeySize = keySize;
            if (iv != null)
            {
                Iv = Encoding.Default.GetBytes(iv);
            }
        }

        public override string ToString()
        {
            return $"{nameof(Generation)}: {Generation}, {nameof(Encryption)}: {Encryption}, {nameof(Iv)}: {Encoding.Default.GetString(Iv)}, {nameof(KeySize)}: {KeySize}";
        }
    }
}