package quan.common.cipher;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Objects;


/**
 * 对称加密器，支持DES、AES算法
 * Created by quanchangnai on 2020/5/21.
 */
public class SymmetricCipher {

    private final SymmetricAlgorithm algorithm;

    private final SecretKey secretKey;

    public SymmetricCipher(SymmetricAlgorithm algorithm) {
        this.algorithm = Objects.requireNonNull(algorithm, "加密算法不能为空");
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.generation);
            keyGenerator.init(algorithm.keySize);
            this.secretKey = keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SymmetricCipher(SymmetricAlgorithm algorithm, byte[] secretKey) {
        this.algorithm = Objects.requireNonNull(algorithm, "加密算法不能为空");
        this.secretKey = new SecretKeySpec(secretKey, algorithm.generation);
    }

    public SymmetricCipher(SymmetricAlgorithm algorithm, String secretKey) {
        this(algorithm, Base64.getDecoder().decode(secretKey));
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
            Cipher cipher = Cipher.getInstance(algorithm.encryption);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, algorithm.getIv());
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密
     */
    public byte[] decrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.encryption);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, algorithm.getIv());
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "SymmetricCipher{" +
                "algorithm=" + algorithm +
                ", secretKey=" + getBase64SecretKey() +
                '}';
    }
}

