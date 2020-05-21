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

    private Algorithm algorithm;

    private PublicKey publicKey;

    private PrivateKey privateKey;

    private AsymmetricCipher(Algorithm algorithm) {
        Objects.requireNonNull(algorithm, "加密算法不能为空");
        this.algorithm = algorithm;
    }

    /**
     * 使用已有秘钥创建密码器
     */
    public static AsymmetricCipher create(Algorithm algorithm, String publicKey, String privateKey) {
        AsymmetricCipher asymmetricCipher = new AsymmetricCipher(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(asymmetricCipher.algorithm.cipher);
            asymmetricCipher.publicKey = keyFactory.generatePublic(publicKeySpec);
            asymmetricCipher.privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return asymmetricCipher;
    }

    /**
     * 使用已有秘钥创建RSA密码器
     */
    public static AsymmetricCipher create(String publicKey, String privateKey) {
        return create(Algorithm.RSA, publicKey, privateKey);
    }

    /**
     * 创建随机秘钥RSA密码器
     */
    public static AsymmetricCipher create(Algorithm algorithm, int keySize) {
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

    /**
     * 创建1024位随机秘钥密码器
     */
    public static AsymmetricCipher create(Algorithm algorithm) {
        return create(algorithm, 1024);
    }

    /**
     * 创建1024位随机秘钥RSA密码器
     */
    public static AsymmetricCipher create() {
        return create(Algorithm.RSA, 1024);
    }

    public Algorithm getAlgorithm() {
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
            Cipher cipher = Cipher.getInstance(algorithm.cipher);
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
            Cipher cipher = Cipher.getInstance(algorithm.cipher);
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
            Cipher cipher = Cipher.getInstance(algorithm.cipher);
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
            Cipher cipher = Cipher.getInstance(algorithm.cipher);
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

    public enum Algorithm {
        RSA("RSA", "RSA/NONE/NoPadding", "MD5withRSA"),
        DSA("DSA", "DSA/NONE/NoPadding", "SHA1withDSA");

        public final String cipher;

        public final String transformation;

        public final String signature;

        Algorithm(String cipher, String transformation, String signature) {
            this.cipher = cipher;
            this.transformation = transformation;
            this.signature = signature;
        }

        @Override
        public String toString() {
            return "Algorithm{" +
                    "cipher='" + cipher + '\'' +
                    ", signature='" + signature + '\'' +
                    '}';
        }

    }


    public static void main(String[] args) {
//        AsymmetricCipher cipher = AsymmetricCipher.create();

        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjoHfRKSPSURhRYJu9UtgkvP2TFjOI4fHbXUmsBgIU1TiNugP/JepY5lMZ6ISF3zg0QO5DTJ09god76BZVFKnKAsWA4Dqcv3bNVUneSZygtsB/SCzjUQ9o8bZkiCd5vaAOER/Z6g75jtl8XGtjE7GtQ/ezd37JQR7xZ2axZrWtdQIDAQAB";
        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKOgd9EpI9JRGFFgm71S2CS8/ZMWM4jh8dtdSawGAhTVOI26A/8l6ljmUxnohIXfODRA7kNMnT2Ch3voFlUUqcoCxYDgOpy/ds1VSd5JnKC2wH9ILONRD2jxtmSIJ3m9oA4RH9nqDvmO2Xxca2MTsa1D97N3fslBHvFnZrFmta11AgMBAAECgYBGaYVmApghpzgZvMMII6BTnuhX5VPj8acMSQas+iDnKiIeCxAxOfWwr9zO51ov6bDb+50MZOm9UHBRB7ykfDHbxkFXCLA3Tesr2/X3+0Iyz30pu1ctCOcjlJaHQpnE09okYI9kZpkTzzK3Hkm59wE/9SOor5Ag70hrXcAApwIceQJBANKXscBScD6nySdmbxBjfPc+UwSEFIwb+XYey3hJHNY9rKuA4opRTTWMB9JaFfgeXeRP8y5Fd+B8Y1R6n9z5KKcCQQDG6FsBtEmj6mVL/ZPr2JeUAo1Z7VH9uB6bG5o2sTeeERAz3GSqZd+K7s/LSjbJ9XbIuGHoFrE4wT+uIfwD9yCDAkAyUVKEXG47WkXC50PES7ExNjAJ1TE/pPN/GK6PKBD+06+tLtdyKyjikXnQ9ftn1IGkqsG1HZ4eAjqNldsapmHjAkApMEZgJPg21Dvjr3/pD7HbuWeR3p3i3zSfQ+j8OFhfCAOF6baCvpO6zlcDLrwHuCe/ysaja8eJDCNmqKzqGUuHAkBlTjExgc35J2TY1pn4FE1pqj8Yavll4vfiDhIrRwLdVJyKq8Sjm4OzVFAkk3rMkqPR/ZL3cxb45RMmtGkYwVQ9";

        AsymmetricCipher cipher = AsymmetricCipher.create(publicKey, privateKey);


        System.err.println("===========publicKey===========");
        System.err.println(cipher.getPublicKey());

        System.err.println("===========privateKey===========");
        System.err.println(cipher.getPrivateKey());

        byte[] encrypted = cipher.encryptByPrivateKey("dadasdaswe".getBytes());
        System.err.println("encrypted:" + Base64.getEncoder().encodeToString(encrypted));

        byte[] decrypted = cipher.decryptByPublicKey(encrypted);
        System.err.println("decrypted:" + new String(decrypted));
    }

}

