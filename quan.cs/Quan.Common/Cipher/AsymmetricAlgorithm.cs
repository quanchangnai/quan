namespace Quan.Common.Cipher
{
    public class AsymmetricAlgorithm
    {
        public static readonly AsymmetricAlgorithm Rsa = new AsymmetricAlgorithm("RSA", "RSA/ECB/PKCS1Padding", "MD5withRSA");

        public static readonly AsymmetricAlgorithm Dsa = new AsymmetricAlgorithm("DSA", null, "SHA1withDSA");

        public readonly string Cipher;

        public readonly string Transformation;

        public readonly string Signature;

        public AsymmetricAlgorithm(string cipher, string transformation, string signature)
        {
            Cipher = cipher;
            Transformation = transformation;
            Signature = signature;
        }
    }
}