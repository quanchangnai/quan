package quan.common.cipher;

public enum AsymmetricAlgorithm {
        RSA("RSA", "RSA/ECB/PKCS1Padding", "MD5withRSA"),
        DSA("DSA", "Unsupported", "SHA1withDSA");

        public final String cipher;

        public final String transformation;

        public final String signature;

        AsymmetricAlgorithm(String cipher, String transformation, String signature) {
            this.cipher = cipher;
            this.transformation = transformation;
            this.signature = signature;
        }

        @Override
        public String toString() {
            return "Algorithm{" +
                    "cipher='" + cipher + '\'' +
                    ", signature='" + signature + '\'' +
                    '}';
        }

    }