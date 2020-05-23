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

    private final PublicKey publicKey;

    private final PrivateKey privateKey;

    public AsymmetricCipher(AsymmetricAlgorithm algorithm) {
        this(algorithm, 1024);
    }

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

    public AsymmetricCipher(AsymmetricAlgorithm algorithm, byte[] publicKey, byte[] privateKey) {
        this.algorithm = Objects.requireNonNull(algorithm, "加密算法不能为空");

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm.generation);
            this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
            this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public AsymmetricCipher(AsymmetricAlgorithm algorithm, String publicKey, String privateKey) {
        this(algorithm, Base64.getDecoder().decode(publicKey), Base64.getDecoder().decode(privateKey));
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
        algorithm.checkEncryption();
        try {
            Cipher cipher = Cipher.getInstance(algorithm.encryption);
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
        algorithm.checkEncryption();
        try {
            Cipher cipher = Cipher.getInstance(algorithm.encryption);
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
        algorithm.checkSignature();
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

