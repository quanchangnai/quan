package quan.common.cipher;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * 对称加密，支持DES、AES算法<br/>
 * Created by quanchangnai on 2020/5/21.
 */
public class SecretKeyCipher {

    private final Algorithm algorithm;

    private final SecretKey secretKey;

    private IvParameterSpec iv;

    /**
     * 随机生成密钥构造
     *
     * @param algorithm 算法
     */
    public SecretKeyCipher(Algorithm algorithm) {
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
    public SecretKeyCipher(Algorithm algorithm, byte[] secretKey) {
        this.algorithm = Objects.requireNonNull(algorithm, "加密算法不能为空");
        this.secretKey = new SecretKeySpec(secretKey, algorithm.generation);
        this.iv = algorithm.getIvParameterSpec();
    }

    /**
     * @see #SecretKeyCipher(Algorithm, byte[])
     */
    public SecretKeyCipher(Algorithm algorithm, String secretKey) {
        this(algorithm, Base64.getDecoder().decode(secretKey));
    }

    /**
     * 自定义初始向量
     */
    public SecretKeyCipher setIv(byte[] iv) {
        if (algorithm.getIv() == null) {
            throw new UnsupportedOperationException("算法[" + algorithm.encryption + "]不支持初始向量");
        }
        if (iv == null || iv.length != algorithm.getIv().length) {
            throw new IllegalArgumentException("初始向量不合法,长度必须为" + algorithm.getIv().length + "个字节");
        }
        this.iv = new IvParameterSpec(iv);
        return this;
    }

    public Algorithm getAlgorithm() {
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
        return "SecretKeyCipher{" +
                "algorithm=" + algorithm +
                ", secretKey=" + getBase64SecretKey() +
                '}';
    }

    /**
     * 对称加密算法枚举
     */
    public enum Algorithm {

        //Data Encryption Standard
        DES("DES", "DES/CBC/PKCS5Padding", "12345678", 56),

        //Triple DES
        DESEde("DESEde", "DESEde/CBC/PKCS5Padding", "12345678", 168),

        //Advanced Encryption Standard
        AES("AES", "AES/CBC/PKCS5Padding", "1234567812345678", 128);


        //密钥生成算法
        public final String generation;

        //加密、解密算法
        public final String encryption;

        //默认初始向量
        private byte[] iv;

        //默认初始向量
        private IvParameterSpec ivParameterSpec;

        //密钥大小
        public final int keySize;

        Algorithm(String generation, String encryption, String iv, int keySize) {
            this.generation = generation;
            this.encryption = encryption;
            this.keySize = keySize;
            if (iv != null) {
                this.iv = iv.getBytes();
                this.ivParameterSpec = new IvParameterSpec(this.iv);
            }
        }

        public byte[] getIv() {
            return iv;
        }

        public IvParameterSpec getIvParameterSpec() {
            return ivParameterSpec;
        }


        @Override
        public String toString() {
            return "SecretKeyCipher.Algorithm{" +
                    "generation='" + generation + '\'' +
                    ", encryption='" + encryption + '\'' +
                    ", iv='" + (iv != null ? new String(iv) : "null") + '\'' +
                    ", keySize=" + keySize +
                    '}';
        }
    }
}

