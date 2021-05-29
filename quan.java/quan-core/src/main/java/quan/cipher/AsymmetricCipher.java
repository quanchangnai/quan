package quan.cipher;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * 非对称加密，支持RSA、DSA算法，支持数字签名<br/>
 * Created by quanchangnai on 2020/5/21.
 */
public class AsymmetricCipher {

    private final Algorithm algorithm;

    private PublicKey publicKey;

    private PrivateKey privateKey;

    /**
     * 随机生成密钥构造，默认密钥长度1024
     *
     * @param algorithm 算法
     */
    public AsymmetricCipher(Algorithm algorithm) {
        this(algorithm, 1024);
    }

    /**
     * 随机生成密钥构造
     *
     * @param algorithm 算法
     * @param keySize   密钥长度
     */
    public AsymmetricCipher(Algorithm algorithm, int keySize) {
        this.algorithm = Objects.requireNonNull(algorithm, "加密算法不能为空");
        KeyPairGenerator keyPairGenerator;

        try {
            keyPairGenerator = KeyPairGenerator.getInstance(algorithm.generation);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    /**
     * 指定已有密钥构造，至少要提供公私钥中的一个
     *
     * @param algorithm  算法
     * @param publicKey  公钥
     * @param privateKey 私钥
     */
    public AsymmetricCipher(Algorithm algorithm, byte[] publicKey, byte[] privateKey) {
        this.algorithm = Objects.requireNonNull(algorithm, "加密算法不能为空");
        if (publicKey == null && privateKey != null) {
            throw new IllegalArgumentException("公钥和私钥不能都为空");
        }

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.generation);
            if (publicKey != null) {
                this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
            }
            if (privateKey != null) {
                this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @see #AsymmetricCipher(Algorithm, byte[], byte[])
     */
    public AsymmetricCipher(Algorithm algorithm, String publicKey, String privateKey) {
        this(algorithm, publicKey == null ? null : Base64.getDecoder().decode(publicKey),
                privateKey == null ? null : Base64.getDecoder().decode(privateKey));
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public byte[] getPublicKey() {
        if (publicKey == null) {
            return null;
        }
        return publicKey.getEncoded();
    }

    public byte[] getPrivateKey() {
        if (privateKey == null) {
            return null;
        }
        return privateKey.getEncoded();
    }

    public String getBase64PublicKey() {
        if (publicKey == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String getBase64PrivateKey() {
        if (privateKey == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    private Cipher encryptor;

    /**
     * 加密
     *
     * @param data       待加密数据
     * @param privateKey 使用私钥还是公钥加密
     * @return 已加密数据
     */
    public byte[] encrypt(byte[] data, boolean privateKey) {
        algorithm.checkEncryption();
        Key key = privateKey ? this.privateKey : this.publicKey;
        if (key == null) {
            throw new IllegalStateException("未设置" + (privateKey ? "私" : "公") + "钥");
        }
        try {
            if (encryptor == null) {
                encryptor = Cipher.getInstance(algorithm.encryption);
                encryptor.init(Cipher.ENCRYPT_MODE, key);
            }
            return encryptor.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Cipher decryptor;

    /**
     * 解密
     *
     * @param data      待解密数据
     * @param publicKey 使用公钥还是私钥解密
     * @return 已解密数据
     */
    public byte[] decrypt(byte[] data, boolean publicKey) {
        algorithm.checkEncryption();
        Key key = publicKey ? this.publicKey : this.privateKey;
        if (key == null) {
            throw new IllegalStateException("未设置" + (publicKey ? "公" : "私") + "钥");
        }
        try {
            if (decryptor == null) {
                decryptor = Cipher.getInstance(algorithm.encryption);
                decryptor.init(Cipher.DECRYPT_MODE, key);
            }
            return decryptor.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Signature signer;

    /**
     * 用私钥签名
     */
    public byte[] sign(byte[] data) {
        algorithm.checkSignature();
        if (privateKey == null) {
            throw new IllegalStateException("未设置私钥");
        }
        try {
            if (signer == null) {
                signer = Signature.getInstance(algorithm.signature);
                signer.initSign(privateKey);
            }
            signer.update(data);
            return signer.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Signature verifier;

    /**
     * 用公钥验签
     */
    public boolean verify(byte[] data, byte[] signature) {
        algorithm.checkSignature();
        if (privateKey == null) {
            throw new IllegalStateException("未设置公钥");
        }
        try {
            if (verifier == null) {
                verifier = Signature.getInstance(algorithm.signature);
                verifier.initVerify(publicKey);
            }
            verifier.update(data);
            return verifier.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "AsymmetricCipher{" +
                "algorithm=" + algorithm +
                ", publicKey=" + getBase64PublicKey() +
                ", privateKey=" + getBase64PrivateKey() +
                '}';
    }

    /**
     * 非对称加密算法枚举
     */
    public enum Algorithm {

        RSA("RSA", "RSA/ECB/PKCS1Padding", "MD5withRSA"),

        //Digital Signature Algorithm
        DSA("DSA", null, "SHA1withDSA");

        //密钥生成算法
        public final String generation;

        //加密、解密算法
        public final String encryption;

        //签名、验签算法
        public final String signature;

        Algorithm(String generation, String encryption, String signature) {
            this.generation = generation;
            this.encryption = encryption;
            this.signature = signature;
        }

        public void checkEncryption() {
            if (encryption == null) {
                throw new UnsupportedOperationException(name() + "算法不支持加密、解密");
            }
        }

        public void checkSignature() {
            if (signature == null) {
                throw new UnsupportedOperationException(name() + "算法不支持签名、验签");
            }
        }

        @Override
        public String toString() {
            return "AsymmetricCipher.Algorithm{" +
                    "generation='" + generation + '\'' +
                    ", encryption='" + encryption + '\'' +
                    ", signature='" + signature + '\'' +
                    '}';
        }
    }
}

