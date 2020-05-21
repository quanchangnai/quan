using System;
using System.IO;
using System.Security.Cryptography;
using System.Text;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Engines;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.OpenSsl;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.X509;

namespace Quan.Common.Cipher
{
    /// <summary>
    /// 非对称加密器
    /// </summary>
    public class AsymmetricCipher
    {
        private readonly string _algorithm = "RSA/ECB/PKCS1Padding";

        private readonly string _publicKey;

        private readonly string _privateKey;

        private readonly AsymmetricKeyParameter _publicKeyParameter;

        private readonly AsymmetricKeyParameter _privateParameter;

        public AsymmetricCipher()
        {
            var provider = new RSACryptoServiceProvider();
            _publicKey = provider.ToXmlString(true);
            _privateKey = provider.ToXmlString(false);
        }

        public AsymmetricCipher(string publicKey, string privateKey)
        {
            _publicKey = publicKey;
            _privateKey = privateKey;
            _publicKeyParameter = PublicKeyFactory.CreateKey(Convert.FromBase64String(publicKey));
            _privateParameter = PrivateKeyFactory.CreateKey(Convert.FromBase64String(privateKey));
        }

        public byte[] EncryptByPublicKey(byte[] data)
        {
            var cipher = CipherUtilities.GetCipher(_algorithm);
            cipher.Init(true, _publicKeyParameter);
            return cipher.DoFinal(data);
        }

        public byte[] DecryptByPublicKey(byte[] data)
        {
            var cipher = CipherUtilities.GetCipher(_algorithm);
            cipher.Init(false, _publicKeyParameter);
            return cipher.DoFinal(data);
        }

        public byte[] EncryptByPrivateKey(byte[] data)
        {
            var cipher = CipherUtilities.GetCipher(_algorithm);
            cipher.Init(true, _privateParameter);
            return cipher.DoFinal(data);
        }

        public byte[] DecryptByPrivateKey(byte[] data)
        {
            var cipher = CipherUtilities.GetCipher(_algorithm);
            cipher.Init(false, _privateParameter);
            return cipher.DoFinal(data);
        }

        public static void Test()
        {
            var publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjoHfRKSPSURhRYJu9UtgkvP2TFjOI4fHbXUmsBgIU1TiNugP/JepY5lMZ6ISF3zg0QO5DTJ09god76BZVFKnKAsWA4Dqcv3bNVUneSZygtsB/SCzjUQ9o8bZkiCd5vaAOER/Z6g75jtl8XGtjE7GtQ/ezd37JQR7xZ2axZrWtdQIDAQAB";
            var privateKey =
                "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKOgd9EpI9JRGFFgm71S2CS8/ZMWM4jh8dtdSawGAhTVOI26A/8l6ljmUxnohIXfODRA7kNMnT2Ch3voFlUUqcoCxYDgOpy/ds1VSd5JnKC2wH9ILONRD2jxtmSIJ3m9oA4RH9nqDvmO2Xxca2MTsa1D97N3fslBHvFnZrFmta11AgMBAAECgYBGaYVmApghpzgZvMMII6BTnuhX5VPj8acMSQas+iDnKiIeCxAxOfWwr9zO51ov6bDb+50MZOm9UHBRB7ykfDHbxkFXCLA3Tesr2/X3+0Iyz30pu1ctCOcjlJaHQpnE09okYI9kZpkTzzK3Hkm59wE/9SOor5Ag70hrXcAApwIceQJBANKXscBScD6nySdmbxBjfPc+UwSEFIwb+XYey3hJHNY9rKuA4opRTTWMB9JaFfgeXeRP8y5Fd+B8Y1R6n9z5KKcCQQDG6FsBtEmj6mVL/ZPr2JeUAo1Z7VH9uB6bG5o2sTeeERAz3GSqZd+K7s/LSjbJ9XbIuGHoFrE4wT+uIfwD9yCDAkAyUVKEXG47WkXC50PES7ExNjAJ1TE/pPN/GK6PKBD+06+tLtdyKyjikXnQ9ftn1IGkqsG1HZ4eAjqNldsapmHjAkApMEZgJPg21Dvjr3/pD7HbuWeR3p3i3zSfQ+j8OFhfCAOF6baCvpO6zlcDLrwHuCe/ysaja8eJDCNmqKzqGUuHAkBlTjExgc35J2TY1pn4FE1pqj8Yavll4vfiDhIrRwLdVJyKq8Sjm4OzVFAkk3rMkqPR/ZL3cxb45RMmtGkYwVQ9";

            var cipher = new AsymmetricCipher(publicKey, privateKey);

            var encrypted = cipher.EncryptByPrivateKey(Encoding.UTF8.GetBytes("dadasdaswe"));
            Console.WriteLine($"encrypted:{Convert.ToBase64String(encrypted)}");
            // var decrypted = cipher.DecryptByPublicKey(encrypted);
            var decrypted = cipher.DecryptByPublicKey(Convert.FromBase64String("G1YUdj8OxVRRLe0AZFmbhQZjvHfrSsTb2IxvxIKLZ42w27G/TyrLiPz0vptKhoNmUKyzWqe53jkmTnF+JcigykElyXnsXFTKMp/eGuCxTHBTvVLZWQNhw/nvmrK+ajAPU15CzV2po0Xj+2SCffsP0+WG/gXjVSeQAuIqeLcLbZM="));

            Console.WriteLine($"decrypted:{Encoding.UTF8.GetString(decrypted)}");
        }
    }
}