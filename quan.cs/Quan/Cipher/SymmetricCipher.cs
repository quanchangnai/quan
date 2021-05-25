using System;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Security;

namespace Quan.Cipher
{
    /// <summary>
    /// 对称加密器
    /// </summary>
    public class SymmetricCipher
    {
        public readonly SymmetricAlgorithm Algorithm;

        private ICipherParameters _keyParameter;

        /// <summary>
        /// 指定密钥构造或者随机生成密钥构造
        /// </summary>
        /// <param name="algorithm">算法</param>
        /// <param name="key">指定的密钥，该参数值为null则随机生成密钥</param>
        public SymmetricCipher(SymmetricAlgorithm algorithm, byte[] key = null)
        {
            Algorithm = algorithm ?? throw new NullReferenceException("加密算法不能为空");

            if (key == null)
            {
                var keyGenerator = GeneratorUtilities.GetKeyGenerator(algorithm.Generation);
                keyGenerator.Init(new KeyGenerationParameters(new SecureRandom(), algorithm.KeySize));
                key = keyGenerator.GenerateKey();
            }

            _keyParameter = ParameterUtilities.CreateKeyParameter(algorithm.Generation, key);
            if (algorithm.Iv != null)
            {
                _keyParameter = new ParametersWithIV(_keyParameter, algorithm.Iv);
            }
        }

        public SymmetricCipher(SymmetricAlgorithm algorithm, string key) : this(algorithm, Convert.FromBase64String(key))
        {
        }

        /// <summary>
        /// 自定义初始向量
        /// </summary>
        public byte[] Iv
        {
            set
            {
                if (Algorithm.Iv == null)
                {
                    throw new ArgumentException($"算法[{Algorithm.Encryption}]不支持设置初始向量");
                }

                var legalLength = Algorithm.Iv.Length;
                if (value == null || value.Length != legalLength)
                {
                    throw new ArgumentException($"初始向量不合法,长度必须为{legalLength}个字节");
                }

                _keyParameter = new ParametersWithIV(_keyParameter, value);
            }
        }

        public byte[] Key
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

        public string Base64Key => Convert.ToBase64String(Key);


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