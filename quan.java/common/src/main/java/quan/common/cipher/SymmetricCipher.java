package quan.common.cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Objects;


/**
 * 对称加密器，支持DES、AES算法
 * Created by quanchangnai on 2020/5/21.
 */
public class SymmetricCipher {

    protected static Logger logger = LoggerFactory.getLogger(SymmetricCipher.class);

    private final SymmetricAlgorithm algorithm;

    private SecretKey secretKey;

    private IvParameterSpec ivParameterSpec;

    private SymmetricCipher(SymmetricAlgorithm algorithm) {
        Objects.requireNonNull(algorithm, "加密算法不能为空");
        this.algorithm = algorithm;
        this.ivParameterSpec = new IvParameterSpec(algorithm.iv.getBytes());
    }

    public static SymmetricCipher create(SymmetricAlgorithm algorithm) {
        SymmetricCipher symmetricCipher = new SymmetricCipher(algorithm);

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.cipher);
            keyGenerator.init(algorithm.keySize);
            symmetricCipher.secretKey = keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return symmetricCipher;
    }

    public static SymmetricCipher create(SymmetricAlgorithm algorithm, byte[] secretKey) {
        SymmetricCipher symmetricCipher = new SymmetricCipher(algorithm);
        symmetricCipher.secretKey = new SecretKeySpec(secretKey, algorithm.cipher);
        return symmetricCipher;
    }

    public static SymmetricCipher create(SymmetricAlgorithm algorithm, String secretKey) {
        return create(algorithm, Base64.getDecoder().decode(secretKey));
    }

    public SymmetricAlgorithm getAlgorithm() {
        return algorithm;
    }

    public byte[] getSecretKey() {
        return secretKey.getEncoded();
    }

    public String getBase64SecretKey() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
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
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

