package quan.common;

import org.junit.Test;
import quan.common.cipher.AsymmetricAlgorithm;
import quan.common.cipher.AsymmetricCipher;
import quan.common.cipher.SymmetricAlgorithm;
import quan.common.cipher.SymmetricCipher;

import java.util.Base64;

/**
 * Created by quanchangnai on 2020/5/22.
 */
public class CipherTest {

    @Test
    public void testAsymmetric1() {
        System.err.println("testAsymmetric1()");

        AsymmetricCipher asymmetricCipher = AsymmetricCipher.create(AsymmetricAlgorithm.RSA);

        System.err.println("===========publicKey===========");
        System.err.println(asymmetricCipher.getPublicKey());

        System.err.println("===========privateKey===========");
        System.err.println(asymmetricCipher.getPrivateKey());

        byte[] encrypted = asymmetricCipher.encryptByPrivateKey("非对称加密测试1".getBytes());
        System.err.println("encrypted:" + Base64.getEncoder().encodeToString(encrypted));

        byte[] decrypted = asymmetricCipher.decryptByPublicKey(encrypted);
        System.err.println("decrypted:" + new String(decrypted));

        System.err.println();
    }

    @Test
    public void testAsymmetric2() {
        System.err.println("testAsymmetric2()");

        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjoHfRKSPSURhRYJu9UtgkvP2TFjOI4fHbXUmsBgIU1TiNugP/JepY5lMZ6ISF3zg0QO5DTJ09god76BZVFKnKAsWA4Dqcv3bNVUneSZygtsB/SCzjUQ9o8bZkiCd5vaAOER/Z6g75jtl8XGtjE7GtQ/ezd37JQR7xZ2axZrWtdQIDAQAB";
        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKOgd9EpI9JRGFFgm71S2CS8/ZMWM4jh8dtdSawGAhTVOI26A/8l6ljmUxnohIXfODRA7kNMnT2Ch3voFlUUqcoCxYDgOpy/ds1VSd5JnKC2wH9ILONRD2jxtmSIJ3m9oA4RH9nqDvmO2Xxca2MTsa1D97N3fslBHvFnZrFmta11AgMBAAECgYBGaYVmApghpzgZvMMII6BTnuhX5VPj8acMSQas+iDnKiIeCxAxOfWwr9zO51ov6bDb+50MZOm9UHBRB7ykfDHbxkFXCLA3Tesr2/X3+0Iyz30pu1ctCOcjlJaHQpnE09okYI9kZpkTzzK3Hkm59wE/9SOor5Ag70hrXcAApwIceQJBANKXscBScD6nySdmbxBjfPc+UwSEFIwb+XYey3hJHNY9rKuA4opRTTWMB9JaFfgeXeRP8y5Fd+B8Y1R6n9z5KKcCQQDG6FsBtEmj6mVL/ZPr2JeUAo1Z7VH9uB6bG5o2sTeeERAz3GSqZd+K7s/LSjbJ9XbIuGHoFrE4wT+uIfwD9yCDAkAyUVKEXG47WkXC50PES7ExNjAJ1TE/pPN/GK6PKBD+06+tLtdyKyjikXnQ9ftn1IGkqsG1HZ4eAjqNldsapmHjAkApMEZgJPg21Dvjr3/pD7HbuWeR3p3i3zSfQ+j8OFhfCAOF6baCvpO6zlcDLrwHuCe/ysaja8eJDCNmqKzqGUuHAkBlTjExgc35J2TY1pn4FE1pqj8Yavll4vfiDhIrRwLdVJyKq8Sjm4OzVFAkk3rMkqPR/ZL3cxb45RMmtGkYwVQ9";
        AsymmetricCipher asymmetricCipher = AsymmetricCipher.create(AsymmetricAlgorithm.RSA, publicKey, privateKey);

        byte[] encrypted = asymmetricCipher.encryptByPrivateKey("非对称加密测试2".getBytes());
        System.err.println("encrypted:" + Base64.getEncoder().encodeToString(encrypted));

        byte[] decrypted = asymmetricCipher.decryptByPublicKey(encrypted);
        System.err.println("decrypted:" + new String(decrypted));

        System.err.println();
    }

    @Test
    public void testSymmetric1() {
        System.err.println("testSymmetric1()");

        SymmetricCipher symmetricCipher = SymmetricCipher.create(SymmetricAlgorithm.DES);
        System.err.println("===========secretKey===========" + symmetricCipher.getSecretKey());

        byte[] encrypted = symmetricCipher.encrypt("对称加密测试1".getBytes());
        System.err.println("encrypted:" + Base64.getEncoder().encodeToString(encrypted));
        byte[] decrypted = symmetricCipher.decrypt(encrypted);
        System.err.println("decrypted:" + new String(decrypted));

        System.err.println();
    }

    @Test
    public void testSymmetric2() {
        System.err.println("testSymmetric2()");

        String secretKey = "7Gvl3xUOJlI=";
        SymmetricCipher symmetricCipher = SymmetricCipher.create(SymmetricAlgorithm.DES, secretKey);

        byte[] encrypted = symmetricCipher.encrypt("对称加密测试2".getBytes());
        System.err.println("encrypted:" + Base64.getEncoder().encodeToString(encrypted));
        byte[] decrypted = symmetricCipher.decrypt(encrypted);
        System.err.println("decrypted:" + new String(decrypted));

        System.err.println();
    }
}
