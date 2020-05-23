package quan.common.cipher;

public enum AsymmetricAlgorithm {

    RSA("RSA", "RSA/ECB/PKCS1Padding", "MD5withRSA"),

    //Digital Signature Algorithm
    DSA("DSA", null, "SHA1withDSA");

    //密钥生成算法
    public final String generation;

    //加密、解密算法
    public final String encryption;

    //签名、验签算法
    public final String signature;

    AsymmetricAlgorithm(String generation, String encryption, String signature) {
        this.generation = generation;
        this.encryption = encryption;
        this.signature = signature;
    }

    public void checkEncryption() {
        if (encryption == null) {
            throw new UnsupportedOperationException(name() + "算法不支持加密、解密");
        }
    }

    public void checkSignature() {
        if (signature == null) {
            throw new UnsupportedOperationException(name() + "算法不支持签名、验签");
        }
    }

    @Override
    public String toString() {
        return "Algorithm{" +
                "generation='" + generation + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }

}