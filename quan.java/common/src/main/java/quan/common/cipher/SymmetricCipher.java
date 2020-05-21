package quan.common.cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Objects;


/**
 * 对称加密器，支持DES、AES算法
 * Created by quanchangnai on 2020/5/21.
 */
public class SymmetricCipher {

    protected static Logger logger = LoggerFactory.getLogger(SymmetricCipher.class);

    private Algorithm algorithm;

    private SecretKey secretKey;

    private IvParameterSpec ivParameterSpec;

    private SymmetricCipher(Algorithm algorithm) {
        Objects.requireNonNull(algorithm, "加密算法不能为空");
        this.algorithm = algorithm;
    }

    /**
     * 创建密码器
     */
    public static SymmetricCipher create(Algorithm algorithm) {
        SymmetricCipher symmetricCipher = new SymmetricCipher(algorithm);

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.cipher);
            keyGenerator.init(algorithm.keySize);
            symmetricCipher.secretKey = keyGenerator.generateKey();
            symmetricCipher.ivParameterSpec = new IvParameterSpec(symmetricCipher.secretKey.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return symmetricCipher;
    }

    /**
     * 创建DES密码器
     */
    public static SymmetricCipher create() {
        return create(Algorithm.DES);
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public byte[] getSecretKey() {
        return secretKey.getEncoded();
    }

    /**
     * 加密
     */
    public byte[] encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     */
    public byte[] decrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.transformation);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(secretKey.getEncoded());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum Algorithm {
        DES("DES", "DES/CBC/PKCS5Padding", 56),
        AES("AES", "AES/CBC/PKCS5PADDING", 128);

        public final String cipher;

        public final String transformation;

        public final int keySize;

        Algorithm(String cipher, String transformation, int keySize) {
            this.cipher = cipher;
            this.transformation = transformation;
            this.keySize = keySize;
        }

        @Override
        public String toString() {
            return "Algorithm{" +
                    "cipher='" + cipher + '\'' +
                    ", transformation='" + transformation + '\'' +
                    ", keySize=" + keySize +
                    '}';
        }

    }

}

