package quan.common.cipher;

public enum SymmetricAlgorithm {

    DES("DES", "DES/CBC/PKCS5Padding", "12345678", 56),

    AES("AES", "AES/CBC/PKCS5Padding", "1234567812345678", 128);

    public final String cipher;

    public final String transformation;

    public final String iv;

    public final int keySize;

    SymmetricAlgorithm(String cipher, String transformation, String iv, int keySize) {
        this.cipher = cipher;
        this.transformation = transformation;
        this.iv = iv;
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