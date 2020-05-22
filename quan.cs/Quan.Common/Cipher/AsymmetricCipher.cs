using System;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Generators;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Pkcs;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.X509;

namespace Quan.Common.Cipher
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

        public AsymmetricCipher() : this(AsymmetricAlgorithm.Rsa)
        {
        }

        public AsymmetricCipher(AsymmetricAlgorithm algorithm, int keySize = 1024)
        {
            Algorithm = algorithm ?? throw new NullReferenceException("加密算法不能为空");
            var keyGenerator = GeneratorUtilities.GetKeyPairGenerator(algorithm.Cipher);
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

        public AsymmetricCipher(AsymmetricAlgorithm algorithm, byte[] publicKey, byte[] privateKey)
        {
            Algorithm = algorithm ?? throw new NullReferenceException("加密算法不能为空");
            _publicKeyParameter = PublicKeyFactory.CreateKey(publicKey);
            _privateKeyParameter = PrivateKeyFactory.CreateKey(privateKey);
            _publicKey = publicKey;
            _privateKey = privateKey;
        }

        public AsymmetricCipher(AsymmetricAlgorithm algorithm, string publicKey, string privateKey) :
            this(algorithm, Convert.FromBase64String(publicKey), Convert.FromBase64String(privateKey))
        {
        }

        public byte[] PublicKey
        {
            get
            {
                if (_publicKey == null)
                {
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
                    _privateKey = PrivateKeyInfoFactory.CreatePrivateKeyInfo(_privateKeyParameter).GetDerEncoded();
                }

                return (byte[]) _privateKey.Clone();
            }
        }

        public string Base64PublicKey => Convert.ToBase64String(PublicKey);

        public string Base64PrivateKey => Convert.ToBase64String(PrivateKey);


        /// <summary>
        /// 加密
        /// </summary>
        public byte[] Encrypt(byte[] data, bool usePrivateKey = true)
        {
            var cipher = CipherUtilities.GetCipher(Algorithm.Transformation);
            cipher.Init(true, usePrivateKey ? _privateKeyParameter : _publicKeyParameter);
            return cipher.DoFinal(data);
        }

        /// <summary>
        /// 解密
        /// </summary>
        public byte[] Decrypt(byte[] data, bool usePublicKey = true)
        {
            var cipher = CipherUtilities.GetCipher(Algorithm.Transformation);
            cipher.Init(false, usePublicKey ? _publicKeyParameter : _privateKeyParameter);
            return cipher.DoFinal(data);
        }

        /// <summary>
        /// 用私钥签名
        /// </summary>
        public byte[] Sign(byte[] data)
        {
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
            var signer = SignerUtilities.GetSigner(Algorithm.Signature);
            signer.Init(false, _publicKeyParameter);
            signer.BlockUpdate(data, 0, data.Length);
            return signer.VerifySignature(signature);
        }
    }
}