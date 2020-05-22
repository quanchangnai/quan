using System;
using System.Text;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Security;

namespace Quan.Common.Cipher
{
    /// <summary>
    /// 对称加密器
    /// </summary>
    public class SymmetricCipher
    {
        private readonly ParametersWithIV _keyParameter;

        public readonly SymmetricAlgorithm Algorithm;

        public SymmetricCipher(SymmetricAlgorithm algorithm, byte[] secretKey = null)
        {
            Algorithm = algorithm ?? throw new NullReferenceException("加密算法不能为空");

            if (secretKey == null)
            {
                var keyGenerator = GeneratorUtilities.GetKeyGenerator(algorithm.Cipher);
                keyGenerator.Init(new KeyGenerationParameters(new SecureRandom(), algorithm.KeySize));
                secretKey = keyGenerator.GenerateKey();
            }

            _keyParameter = new ParametersWithIV(ParameterUtilities.CreateKeyParameter(algorithm.Cipher, secretKey), Encoding.UTF8.GetBytes(algorithm.Iv));
        }

        public SymmetricCipher(SymmetricAlgorithm algorithm, string secretKey)
        {
            Algorithm = algorithm ?? throw new NullReferenceException("加密算法不能为空");
            _keyParameter = new ParametersWithIV(ParameterUtilities.CreateKeyParameter(algorithm.Cipher, Convert.FromBase64String(secretKey)), Encoding.UTF8.GetBytes(algorithm.Iv));
        }

        public byte[] SecretKey => ((KeyParameter) _keyParameter.Parameters).GetKey();

        public string Base64SecretKey => Convert.ToBase64String(SecretKey);


        /// <summary>
        /// 加密
        /// </summary>
        public byte[] Encrypt(byte[] data)
        {
            var cipher = CipherUtilities.GetCipher(Algorithm.Transformation);
            cipher.Init(true, _keyParameter);
            return cipher.DoFinal(data);
        }

        /// <summary>
        /// 解密
        /// </summary>
        public byte[] Decrypt(byte[] data)
        {
            var cipher = CipherUtilities.GetCipher(Algorithm.Transformation);
            cipher.Init(false, _keyParameter);
            return cipher.DoFinal(data);
        }
    }
}