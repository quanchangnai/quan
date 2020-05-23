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
        private readonly ICipherParameters _keyParameter;

        public readonly SymmetricAlgorithm Algorithm;

        public SymmetricCipher() : this(SymmetricAlgorithm.Des)
        {
        }

        public SymmetricCipher(SymmetricAlgorithm algorithm, byte[] secretKey = null)
        {
            Algorithm = algorithm ?? throw new NullReferenceException("加密算法不能为空");

            if (secretKey == null)
            {
                var keyGenerator = GeneratorUtilities.GetKeyGenerator(algorithm.Generation);
                keyGenerator.Init(new KeyGenerationParameters(new SecureRandom(), algorithm.KeySize));
                secretKey = keyGenerator.GenerateKey();
            }

            _keyParameter = ParameterUtilities.CreateKeyParameter(algorithm.Generation, secretKey);

            if (algorithm.Iv != null)
            {
                _keyParameter = new ParametersWithIV(_keyParameter, Encoding.UTF8.GetBytes(algorithm.Iv));
            }
        }

        public SymmetricCipher(SymmetricAlgorithm algorithm, string secretKey) : this(algorithm, Convert.FromBase64String(secretKey))
        {
        }

        public SymmetricCipher(string secretKey) : this(SymmetricAlgorithm.Des, secretKey)
        {
        }

        public byte[] SecretKey
        {
            get
            {
                if (_keyParameter is KeyParameter parameter)
                {
                    return parameter.GetKey();
                }

                return ((KeyParameter) ((ParametersWithIV) _keyParameter).Parameters).GetKey();
            }
        }

        public string Base64SecretKey => Convert.ToBase64String(SecretKey);


        /// <summary>
        /// 加密
        /// </summary>
        public byte[] Encrypt(byte[] data)
        {
            var cipher = CipherUtilities.GetCipher(Algorithm.Encryption);
            cipher.Init(true, _keyParameter);
            return cipher.DoFinal(data);
        }

        /// <summary>
        /// 解密
        /// </summary>
        public byte[] Decrypt(byte[] data)
        {
            var cipher = CipherUtilities.GetCipher(Algorithm.Encryption);
            cipher.Init(false, _keyParameter);
            return cipher.DoFinal(data);
        }
    }
}