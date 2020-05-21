package quan.common.cipher;

public enum SymmetricAlgorithm {
        DES("DES", "DES/CBC/PKCS5Padding", 56),
        AES("AES", "AES/CBC/PKCS5PADDING", 128);

        public final String cipher;

        public final String transformation;

        public final int keySize;

        SymmetricAlgorithm(String cipher, String transformation, int keySize) {
            this.cipher = cipher;
            this.transformation = transformation;
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