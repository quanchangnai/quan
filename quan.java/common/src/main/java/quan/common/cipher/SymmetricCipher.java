package quan.common.cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
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
    }

    public static SymmetricCipher create(SymmetricAlgorithm algorithm) {
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

    public static SymmetricCipher create(SymmetricAlgorithm algorithm, byte[] secretKey) {
        SymmetricCipher symmetricCipher = new SymmetricCipher(algorithm);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, algorithm.cipher);

        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm.cipher);
            symmetricCipher.secretKey = secretKeyFactory.generateSecret(secretKeySpec);
            symmetricCipher.ivParameterSpec = new IvParameterSpec(symmetricCipher.secretKey.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return symmetricCipher;
    }

    public static SymmetricCipher create(SymmetricAlgorithm algorithm, String secretKey) {
        return create(algorithm, Base64.getDecoder().decode(secretKey));
    }

    public SymmetricAlgorithm getAlgorithm() {
        return algorithm;
    }

    public String getSecretKey() {
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
            IvParameterSpec ivParameterSpec = new IvParameterSpec(secretKey.getEncoded());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

