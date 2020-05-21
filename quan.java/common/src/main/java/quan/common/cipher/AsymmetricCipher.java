package quan.common.cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;


/**
 * 非对称加密器，支持RSA、DES算法，支持数字签名
 * Created by quanchangnai on 2020/5/21.
 */
public class AsymmetricCipher {

    protected static Logger logger = LoggerFactory.getLogger(AsymmetricCipher.class);

    private final AsymmetricAlgorithm algorithm;

    private PublicKey publicKey;

    private PrivateKey privateKey;

    private AsymmetricCipher(AsymmetricAlgorithm algorithm) {
        Objects.requireNonNull(algorithm, "加密算法不能为空");
        this.algorithm = algorithm;
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
            e.printStackTrace();
            return null;
        }

        return asymmetricCipher;
    }


    public static AsymmetricCipher create(AsymmetricAlgorithm algorithm, String publicKey, String privateKey) {
        return create(algorithm, Base64.getDecoder().decode(publicKey), Base64.getDecoder().decode(privateKey));
    }

    public static AsymmetricCipher create(AsymmetricAlgorithm algorithm, int keySize) {
        AsymmetricCipher asymmetricCipher = new AsymmetricCipher(algorithm);
        KeyPairGenerator keyPairGenerator;

        try {
            keyPairGenerator = KeyPairGenerator.getInstance(algorithm.cipher);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        asymmetricCipher.publicKey = keyPair.getPublic();
        asymmetricCipher.privateKey = keyPair.getPrivate();

        return asymmetricCipher;
    }

    public static AsymmetricCipher create(AsymmetricAlgorithm algorithm) {
        return create(algorithm, 1024);
    }

    public AsymmetricAlgorithm getAlgorithm() {
        return algorithm;
    }

    public String getPrivateKey() {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }


    /**
     * 用公钥加密
     */
    public byte[] encryptByPublicKey(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.transformation);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 用公钥解密
     */
    public byte[] decryptByPublicKey(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.transformation);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥加密
     */
    public byte[] encryptByPrivateKey(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.transformation);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥解密
     */
    public byte[] decryptByPrivateKey(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.transformation);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用私钥签名
     */
    public byte[] sign(byte[] data) {
        try {
            Signature signature = Signature.getInstance(algorithm.signature);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用公钥验签
     */
    public boolean verify(byte[] data, byte[] sign) {
        try {
            Signature signature = Signature.getInstance(algorithm.signature);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "RsaCipher{" +
                "algorithm='" + algorithm + '\'' +
                ", publicKey=" + getPublicKey() +
                ", privateKey=" + getPrivateKey() +
                '}';
    }



}

