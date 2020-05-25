package quan.common.cipher;

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

    private final SymmetricAlgorithm algorithm;

    private final SecretKey secretKey;

    private IvParameterSpec iv;

    /**
     * 随机生成密钥构造
     *
     * @param algorithm 算法
     */
    public SymmetricCipher(SymmetricAlgorithm algorithm) {
        this.algorithm = Objects.requireNonNull(algorithm, "加密算法不能为空");
        this.iv = algorithm.getIvParameterSpec();
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.generation);
            keyGenerator.init(algorithm.keySize);
            this.secretKey = keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 指定已有密钥构造
     *
     * @param algorithm 算法
     * @param secretKey 密钥
     */
    public SymmetricCipher(SymmetricAlgorithm algorithm, byte[] secretKey) {
        this.algorithm = Objects.requireNonNull(algorithm, "加密算法不能为空");
        this.secretKey = new SecretKeySpec(secretKey, algorithm.generation);
        this.iv = algorithm.getIvParameterSpec();
    }

    /**
     * @see #SymmetricCipher(SymmetricAlgorithm, byte[])
     */
    public SymmetricCipher(SymmetricAlgorithm algorithm, String secretKey) {
        this(algorithm, Base64.getDecoder().decode(secretKey));
    }

    /**
     * 自定义初始向量
     */
    public SymmetricCipher setIv(byte[] iv) {
        if (algorithm.getIv() == null) {
            throw new UnsupportedOperationException("算法[" + algorithm.encryption + "]不支持初始向量");
        }
        if (iv == null || iv.length != algorithm.getIv().length) {
            throw new IllegalArgumentException("初始向量不合法,长度必须为" + algorithm.getIv().length + "个字节");
        }
        this.iv = new IvParameterSpec(iv);
        return this;
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
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
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
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
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

