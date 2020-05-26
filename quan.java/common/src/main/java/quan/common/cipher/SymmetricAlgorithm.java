package quan.common.cipher;

import javax.crypto.spec.IvParameterSpec;

public enum SymmetricAlgorithm {

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

    SymmetricAlgorithm(String generation, String encryption, String iv, int keySize) {
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
        return "SymmetricAlgorithm{" +
                "generation='" + generation + '\'' +
                ", encryption='" + encryption + '\'' +
                ", iv='" + (iv != null ? new String(iv) : "null") + '\'' +
                ", keySize=" + keySize +
                '}';
    }
}