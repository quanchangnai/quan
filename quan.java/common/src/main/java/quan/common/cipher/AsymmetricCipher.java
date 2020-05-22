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

    private AsymmetricCipher(AsymmetricAlgorithm algorithm) {
        Objects.requireNonNull(algorithm, "加密算法不能为空");
        this.algorithm = algorithm;
    }

    public static AsymmetricCipher create() {
        return create(AsymmetricAlgorithm.RSA, 1024);
    }

    public static AsymmetricCipher create(AsymmetricAlgorithm algorithm) {
        return create(algorithm, 1024);
    }

    public static AsymmetricCipher create(AsymmetricAlgorithm algorithm, int keySize) {
        AsymmetricCipher asymmetricCipher = new AsymmetricCipher(algorithm);
        KeyPairGenerator keyPairGenerator;

        try {
            keyPairGenerator = KeyPairGenerator.getInstance(algorithm.cipher);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        asymmetricCipher.publicKey = keyPair.getPublic();
        asymmetricCipher.privateKey = keyPair.getPrivate();

        return asymmetricCipher;
    }

    public static AsymmetricCipher create(AsymmetricAlgorithm algorithm, byte[] publicKey, byte[] privateKey) {
        AsymmetricCipher asymmetricCipher = new AsymmetricCipher(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.cipher);
            asymmetricCipher.publicKey = keyFactory.generatePublic(publicKeySpec);
            asymmetricCipher.privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return asymmetricCipher;
    }


    public static AsymmetricCipher create(AsymmetricAlgorithm algorithm, String publicKey, String privateKey) {
        return create(algorithm, Base64.getDecoder().decode(publicKey), Base64.getDecoder().decode(privateKey));
    }

    public AsymmetricAlgorithm getAlgorithm() {
        return algorithm;
    }

    public byte[] getPublicKey() {
        return publicKey.getEncoded();
    }

    public byte[] getPrivateKey() {
        return privateKey.getEncoded();
    }

    public String getBase64PublicKey() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String getBase64PrivateKey() {
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
        try {
            Cipher cipher = Cipher.getInstance(algorithm.transformation);
            cipher.init(Cipher.ENCRYPT_MODE, usePrivateKey ? privateKey : publicKey);
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
        try {
            Cipher cipher = Cipher.getInstance(algorithm.transformation);
            cipher.init(Cipher.DECRYPT_MODE, usePublicKey ? publicKey : privateKey);
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
        return "RsaCipher{" +
                "algorithm='" + algorithm + '\'' +
                ", publicKey=" + getBase64PublicKey() +
                ", privateKey=" + getBase64PrivateKey() +
                '}';
    }

}

