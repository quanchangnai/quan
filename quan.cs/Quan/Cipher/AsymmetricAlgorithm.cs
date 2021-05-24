using System;

namespace Quan.Cipher
{
    public class AsymmetricAlgorithm
    {
        public static readonly AsymmetricAlgorithm Rsa = new AsymmetricAlgorithm("RSA", "RSA/ECB/PKCS1Padding", "MD5withRSA");

        //Digital Signature Algorithm
        public static readonly AsymmetricAlgorithm Dsa = new AsymmetricAlgorithm("DSA", null, "SHA1withDSA");

        //密钥生成算法
        public readonly string Generation;

        //加密、解密算法
        public readonly string Encryption;

        //签名、验签算法
        public readonly string Signature;

        private AsymmetricAlgorithm(string generation, string encryption, string signature)
        {
            Generation = generation;
            Encryption = encryption;
            Signature = signature;
        }

        public void CheckEncrypt()
        {
            if (Encryption == null)
            {
                throw new NotSupportedException($"{Generation}算法不支持加密、解密");
            }
        }

        public void CheckSign()
        {
            if (Signature == null)
            {
                throw new NotSupportedException($"{Generation}算法不支持签名、验签");
            }
        }

        public override string ToString()
        {
            return $"{nameof(Generation)}: {Generation}, {nameof(Encryption)}: {Encryption}, {nameof(Signature)}: {Signature}";
        }
    }
}