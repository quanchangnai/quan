using System;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Generators;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Pkcs;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.X509;

namespace Quan.Cipher
{
    /// <summary>
    /// 非对称加密器
    /// </summary>
    public class AsymmetricCipher
    {
        public readonly AsymmetricAlgorithm Algorithm;

        private byte[] _publicKey;

        private byte[] _privateKey;

        private readonly AsymmetricKeyParameter _publicKeyParameter;

        private readonly AsymmetricKeyParameter _privateKeyParameter;

        /// <summary>
        /// 随机生成密钥构造，默认密钥长度1024
        /// </summary>
        public AsymmetricCipher(AsymmetricAlgorithm algorithm, int keySize = 1024)
        {
            Algorithm = algorithm ?? throw new NullReferenceException("加密算法不能为空");
            var keyGenerator = GeneratorUtilities.GetKeyPairGenerator(algorithm.Generation);
            var random = new SecureRandom();

            if (algorithm == AsymmetricAlgorithm.Dsa)
            {
                var dsaParametersGenerator = new DsaParametersGenerator();
                dsaParametersGenerator.Init(keySize, 100, random);
                keyGenerator.Init(new DsaKeyGenerationParameters(random, dsaParametersGenerator.GenerateParameters()));
            }
            else
            {
                keyGenerator.Init(new KeyGenerationParameters(new SecureRandom(), keySize));
            }

            var keyPair = keyGenerator.GenerateKeyPair();
            _publicKeyParameter = keyPair.Public;
            _privateKeyParameter = keyPair.Private;
        }

        /// <summary>
        /// 指定已有密钥构造，至少要提供公私钥中的一个
        /// </summary>
        public AsymmetricCipher(AsymmetricAlgorithm algorithm, byte[] publicKey, byte[] privateKey)
        {
            Algorithm = algorithm ?? throw new NullReferenceException("加密算法不能为空");
            if (publicKey == null && privateKey == null)
            {
                throw new NullReferenceException("公钥和私钥不能都为空");
            }

            if (publicKey != null)
            {
                _publicKeyParameter = PublicKeyFactory.CreateKey(publicKey);
                _publicKey = publicKey;
            }

            if (privateKey != null)
            {
                _privateKeyParameter = PrivateKeyFactory.CreateKey(privateKey);
                _privateKey = privateKey;
            }
        }

        
        /// <summary>
        /// 指定已有密钥构造，至少要提供公私钥中的一个
        /// </summary>
        public AsymmetricCipher(AsymmetricAlgorithm algorithm, string publicKey, string privateKey) :
            this(algorithm, publicKey != null ? Convert.FromBase64String(publicKey) : null, privateKey != null ? Convert.FromBase64String(privateKey) : null)
        {
        }

        public byte[] PublicKey
        {
            get
            {
                if (_publicKey == null)
                {
                    if (_publicKeyParameter == null)
                    {
                        return null;
                    }

                    _publicKey = SubjectPublicKeyInfoFactory.CreateSubjectPublicKeyInfo(_publicKeyParameter).GetDerEncoded();
                }

                return (byte[]) _publicKey.Clone();
            }
        }

        public byte[] PrivateKey
        {
            get
            {
                if (_privateKey == null)
                {
                    if (_privateKeyParameter == null)
                    {
                        return null;
                    }

                    _privateKey = PrivateKeyInfoFactory.CreatePrivateKeyInfo(_privateKeyParameter).GetDerEncoded();
                }

                return (byte[]) _privateKey.Clone();
            }
        }

        public string Base64PublicKey => PublicKey != null ? Convert.ToBase64String(PublicKey) : null;

        public string Base64PrivateKey => PrivateKey != null ? Convert.ToBase64String(PrivateKey) : null;


        /// <summary>
        /// 加密
        /// </summary>
        public byte[] Encrypt(byte[] data, bool usePrivateKey = true)
        {
            Algorithm.CheckEncrypt();
            var keyParameters = usePrivateKey ? _privateKeyParameter : _publicKeyParameter;
            if (keyParameters == null)
            {
                throw new ArgumentException($"未设置{(usePrivateKey ? '私' : '公')}钥");
            }

            var cipher = CipherUtilities.GetCipher(Algorithm.Encryption);
            cipher.Init(true, keyParameters);
            return cipher.DoFinal(data);
        }

        /// <summary>
        /// 解密
        /// </summary>
        public byte[] Decrypt(byte[] data, bool usePublicKey = true)
        {
            Algorithm.CheckEncrypt();
            var keyParameters = usePublicKey ? _privateKeyParameter : _publicKeyParameter;
            if (keyParameters == null)
            {
                throw new ArgumentException($"未设置{(usePublicKey ? '公' : '私')}钥");
            }

            var cipher = CipherUtilities.GetCipher(Algorithm.Encryption);
            cipher.Init(false, usePublicKey ? _publicKeyParameter : _privateKeyParameter);
            return cipher.DoFinal(data);
        }

        /// <summary>
        /// 用私钥签名
        /// </summary>
        public byte[] Sign(byte[] data)
        {
            Algorithm.CheckSign();
            if (_privateKeyParameter == null)
            {
                throw new ArgumentException("未设置私钥");
            }

            var signer = SignerUtilities.GetSigner(Algorithm.Signature);
            signer.Init(true, _privateKeyParameter);
            signer.BlockUpdate(data, 0, data.Length);
            return signer.GenerateSignature();
        }

        /// <summary>
        /// 用公钥验签
        /// </summary>
        public bool Verify(byte[] data, byte[] signature)
        {
            Algorithm.CheckSign();
            if (_privateKeyParameter == null)
            {
                throw new ArgumentException("未设置公钥");
            }

            var signer = SignerUtilities.GetSigner(Algorithm.Signature);
            signer.Init(false, _publicKeyParameter);
            signer.BlockUpdate(data, 0, data.Length);
            return signer.VerifySignature(signature);
        }
    }
}