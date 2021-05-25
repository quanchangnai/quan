package quan.cipher;

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
public class SymmetricCipher {

    private final Algorithm algorithm;

    private final SecretKey key;

    private IvParameterSpec iv;

    /**
     * 随机生成密钥构造
     *
     * @param algorithm 算法
     */
    public SymmetricCipher(Algorithm algorithm) {
        this.algorithm = Objects.requireNonNull(algorithm, "加密算法不能为空");
        this.iv = algorithm.getIvParameterSpec();
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.generation);
            keyGenerator.init(algorithm.keySize);
            this.key = keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 指定已有密钥构造
     *
     * @param algorithm 算法
     * @param key       密钥
     */
    public SymmetricCipher(Algorithm algorithm, byte[] key) {
        this.algorithm = Objects.requireNonNull(algorithm, "加密算法不能为空");
        this.key = new SecretKeySpec(key, algorithm.generation);
        this.iv = algorithm.getIvParameterSpec();
    }

    /**
     * @see #SymmetricCipher(Algorithm, byte[])
     */
    public SymmetricCipher(Algorithm algorithm, String key) {
        this(algorithm, Base64.getDecoder().decode(key));
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

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public byte[] getKey() {
        return key.getEncoded();
    }

    public String getBase64Key() {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * 加密
     */
    public byte[] encrypt(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm.encryption);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
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
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "SymmetricCipher{" +
            "algorithm=" + algorithm +
            ", key=" + getBase64Key() +
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
            return "SymmetricCipher.Algorithm{" +
                "generation='" + generation + '\'' +
                ", encryption='" + encryption + '\'' +
                ", iv='" + (iv != null ? new String(iv) : "null") + '\'' +
                ", keySize=" + keySize +
                '}';
        }
    }
}

