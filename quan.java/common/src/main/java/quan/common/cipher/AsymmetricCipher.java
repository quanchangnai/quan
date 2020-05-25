package quan.common.cipher;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;


/**
 * 非对称加密器，支持RSA、DSA算法，支持数字签名
 * Created by quanchangnai on 2020/5/21.
 */
public class AsymmetricCipher {

    private final AsymmetricAlgorithm algorithm;

    private PublicKey publicKey;

    private PrivateKey privateKey;

    /**
     * 随机生成密钥构造，默认密钥长度1024
     *
     * @param algorithm 算法
     */
    public AsymmetricCipher(AsymmetricAlgorithm algorithm) {
        this(algorithm, 1024);
    }

    /**
     * 随机生成密钥构造
     *
     * @param algorithm 算法
     * @param keySize   密钥长度
     */
    public AsymmetricCipher(AsymmetricAlgorithm algorithm, int keySize) {
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
    public AsymmetricCipher(AsymmetricAlgorithm algorithm, byte[] publicKey, byte[] privateKey) {
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
     * @see #AsymmetricCipher(AsymmetricAlgorithm, byte[], byte[])
     */
    public AsymmetricCipher(AsymmetricAlgorithm algorithm, String publicKey, String privateKey) {
        this(algorithm, publicKey != null ? Base64.getDecoder().decode(publicKey) : null, privateKey != null ? Base64.getDecoder().decode(privateKey) : null);
    }

    public AsymmetricAlgorithm getAlgorithm() {
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

    /**
     * 加密
     *
     * @param data          待加密数据
     * @param usePrivateKey 使用私钥还是公钥加密
     * @return 已加密数据
     */
    public byte[] encrypt(byte[] data, boolean usePrivateKey) {
        algorithm.checkEncryption();
        Key key = usePrivateKey ? privateKey : publicKey;
        if (key == null) {
            throw new IllegalStateException("未设置" + (usePrivateKey ? "私" : "公") + "钥");
        }
        try {
            Cipher cipher = Cipher.getInstance(algorithm.encryption);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 使用私钥加密
     */
    public byte[] encrypt(byte[] data) {
        return encrypt(data, true);
    }


    /**
     * 解密
     *
     * @param data         待解密数据
     * @param usePublicKey 使用公钥还是私钥解密
     * @return 已解密数据
     */
    public byte[] decrypt(byte[] data, boolean usePublicKey) {
        algorithm.checkEncryption();
        Key key = usePublicKey ? publicKey : privateKey;
        if (key == null) {
            throw new IllegalStateException("未设置" + (usePublicKey ? "公" : "私") + "钥");
        }
        try {
            Cipher cipher = Cipher.getInstance(algorithm.encryption);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用公钥解密
     */
    public byte[] decrypt(byte[] data) {
        return decrypt(data, true);
    }


    /**
     * 用私钥签名
     */
    public byte[] sign(byte[] data) {
        algorithm.checkSignature();
        if (privateKey == null) {
            throw new IllegalStateException("未设置私钥");
        }
        try {
            Signature signer = Signature.getInstance(algorithm.signature);
            signer.initSign(privateKey);
            signer.update(data);
            return signer.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用公钥验签
     */
    public boolean verify(byte[] data, byte[] signature) {
        algorithm.checkSignature();
        if (privateKey == null) {
            throw new IllegalStateException("未设置公钥");
        }
        try {
            Signature signer = Signature.getInstance(algorithm.signature);
            signer.initVerify(publicKey);
            signer.update(data);
            return signer.verify(signature);
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

}

