namespace Quan.Common.Cipher
{
    public class SymmetricAlgorithm
    {
        public static readonly SymmetricAlgorithm Des = new SymmetricAlgorithm("DES", "DES/CBC/PKCS5Padding", "12345678", 56);

        public static readonly SymmetricAlgorithm Aes = new SymmetricAlgorithm("AES", "AES/CBC/PKCS5Padding", "1234567812345678", 128);

        public readonly string Cipher;

        public readonly string Transformation;

        public readonly string Iv;

        public readonly int KeySize;

        public SymmetricAlgorithm(string cipher, string transformation, string iv, int keySize)
        {
            Cipher = cipher;
            Transformation = transformation;
            Iv = iv;
            KeySize = keySize;
        }

        public override string ToString()
        {
            return $"{nameof(Cipher)}: {Cipher}, {nameof(Transformation)}: {Transformation}, {nameof(Iv)}: {Iv}, {nameof(KeySize)}: {KeySize}";
        }
    }
}