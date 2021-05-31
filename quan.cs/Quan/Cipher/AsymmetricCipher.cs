using System;
using System.Diagnostics;
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


        private IBufferedCipher _privateEncryptCipher;
        private IBufferedCipher _publicEncryptCipher;

        /// <summary>
        /// 加密
        /// </summary>
        public byte[] Encrypt(byte[] data, bool privateKey)
        {
            Algorithm.CheckEncrypt();
            var keyParameters = privateKey ? _privateKeyParameter : _publicKeyParameter;
            if (keyParameters == null)
            {
                throw new ArgumentException($"未设置{(privateKey ? '私' : '公')}钥");
            }

            if (_privateEncryptCipher == null && _privateKeyParameter != null)
            {
                _privateEncryptCipher = CipherUtilities.GetCipher(Algorithm.Encryption);
                _privateEncryptCipher.Init(true, _privateKeyParameter);
            }

            if (_publicEncryptCipher == null && _publicKeyParameter != null)
            {
                _publicEncryptCipher = CipherUtilities.GetCipher(Algorithm.Encryption);
                _publicEncryptCipher.Init(true, _publicKeyParameter);
            }

            if (privateKey)
            {
                Debug.Assert(_privateEncryptCipher != null, nameof(_privateEncryptCipher) + " != null");
                return _privateEncryptCipher.DoFinal(data);
            }

            Debug.Assert(_publicEncryptCipher != null, nameof(_publicEncryptCipher) + " != null");
            return _publicEncryptCipher.DoFinal(data);
        }

        private IBufferedCipher _privateDecryptCipher;
        private IBufferedCipher _publicDecryptCipher;

        /// <summary>
        /// 解密
        /// </summary>
        public byte[] Decrypt(byte[] data, bool publicKey)
        {
            Algorithm.CheckEncrypt();
            var keyParameters = publicKey ? _publicKeyParameter : _privateKeyParameter;
            if (keyParameters == null)
            {
                throw new ArgumentException($"未设置{(publicKey ? '公' : '私')}钥");
            }

            if (_privateDecryptCipher == null && _privateKeyParameter != null)
            {
                _privateDecryptCipher = CipherUtilities.GetCipher(Algorithm.Encryption);
                _privateDecryptCipher.Init(false, _privateKeyParameter);
            }

            if (_publicDecryptCipher == null && _publicKeyParameter != null)
            {
                _publicDecryptCipher = CipherUtilities.GetCipher(Algorithm.Encryption);
                _publicDecryptCipher.Init(false, _publicKeyParameter);
            }

            if (publicKey)
            {
                Debug.Assert(_publicDecryptCipher != null, nameof(_publicDecryptCipher) + " != null");
                return _publicDecryptCipher.DoFinal(data);
            }

            Debug.Assert(_privateDecryptCipher != null, nameof(_privateDecryptCipher) + " != null");
            return _privateDecryptCipher.DoFinal(data);
        }


        private ISigner _signer;


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

            if (_signer == null)
            {
                _signer = SignerUtilities.GetSigner(Algorithm.Signature);
                _signer.Init(true, _privateKeyParameter);
            }

            _signer.BlockUpdate(data, 0, data.Length);
            return _signer.GenerateSignature();
        }


        private ISigner _verifier;

        /// <summary>
        /// 用公钥验签
        /// </summary>
        public bool Verify(byte[] data, byte[] signature)
        {
            Algorithm.CheckSign();
            if (_publicKeyParameter == null)
            {
                throw new ArgumentException("未设置公钥");
            }

            if (_verifier == null)
            {
                _verifier = SignerUtilities.GetSigner(Algorithm.Signature);
                _verifier.Init(false, _publicKeyParameter);
            }

            _verifier.BlockUpdate(data, 0, data.Length);
            return _verifier.VerifySignature(signature);
        }
    }
}