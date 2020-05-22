package quan.common;

import org.junit.Assert;
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
    public void testAsymmetricEncrypt1() {
        String data = "非对称加密测试1";
        System.err.println(data);

        AsymmetricCipher asymmetricCipher = AsymmetricCipher.create(AsymmetricAlgorithm.RSA);

        System.err.println("===========publicKey===========");
        System.err.println(asymmetricCipher.getBase64PublicKey());

        System.err.println("===========privateKey===========");
        System.err.println(asymmetricCipher.getBase64PrivateKey());

        byte[] encrypted = asymmetricCipher.encrypt(data.getBytes(), true);
        System.err.println("encrypted:" + Base64.getEncoder().encodeToString(encrypted));

        String decrypted = new String(asymmetricCipher.decrypt(encrypted, true));
        System.err.println("decrypted:" + decrypted);

        Assert.assertEquals(data, decrypted);
        System.err.println();
    }

    @Test
    public void testAsymmetricEncrypt2() {
        String data = "非对称加密测试2";
        System.err.println(data);

        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjoHfRKSPSURhRYJu9UtgkvP2TFjOI4fHbXUmsBgIU1TiNugP/JepY5lMZ6ISF3zg0QO5DTJ09god76BZVFKnKAsWA4Dqcv3bNVUneSZygtsB/SCzjUQ9o8bZkiCd5vaAOER/Z6g75jtl8XGtjE7GtQ/ezd37JQR7xZ2axZrWtdQIDAQAB";
        String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKOgd9EpI9JRGFFgm71S2CS8/ZMWM4jh8dtdSawGAhTVOI26A/8l6ljmUxnohIXfODRA7kNMnT2Ch3voFlUUqcoCxYDgOpy/ds1VSd5JnKC2wH9ILONRD2jxtmSIJ3m9oA4RH9nqDvmO2Xxca2MTsa1D97N3fslBHvFnZrFmta11AgMBAAECgYBGaYVmApghpzgZvMMII6BTnuhX5VPj8acMSQas+iDnKiIeCxAxOfWwr9zO51ov6bDb+50MZOm9UHBRB7ykfDHbxkFXCLA3Tesr2/X3+0Iyz30pu1ctCOcjlJaHQpnE09okYI9kZpkTzzK3Hkm59wE/9SOor5Ag70hrXcAApwIceQJBANKXscBScD6nySdmbxBjfPc+UwSEFIwb+XYey3hJHNY9rKuA4opRTTWMB9JaFfgeXeRP8y5Fd+B8Y1R6n9z5KKcCQQDG6FsBtEmj6mVL/ZPr2JeUAo1Z7VH9uB6bG5o2sTeeERAz3GSqZd+K7s/LSjbJ9XbIuGHoFrE4wT+uIfwD9yCDAkAyUVKEXG47WkXC50PES7ExNjAJ1TE/pPN/GK6PKBD+06+tLtdyKyjikXnQ9ftn1IGkqsG1HZ4eAjqNldsapmHjAkApMEZgJPg21Dvjr3/pD7HbuWeR3p3i3zSfQ+j8OFhfCAOF6baCvpO6zlcDLrwHuCe/ysaja8eJDCNmqKzqGUuHAkBlTjExgc35J2TY1pn4FE1pqj8Yavll4vfiDhIrRwLdVJyKq8Sjm4OzVFAkk3rMkqPR/ZL3cxb45RMmtGkYwVQ9";
        AsymmetricCipher asymmetricCipher = AsymmetricCipher.create(AsymmetricAlgorithm.RSA, publicKey, privateKey);

        byte[] encrypted = asymmetricCipher.encrypt(data.getBytes(), true);
        System.err.println("encrypted:" + Base64.getEncoder().encodeToString(encrypted));

        String decrypted = new String(asymmetricCipher.decrypt(encrypted, true));
        System.err.println("decrypted:" + decrypted);

        Assert.assertEquals(data, decrypted);
        System.err.println();
    }

    @Test
    public void testAsymmetricSign1() {
        String data = "非对称签名测试1";
        System.err.println(data);

        AsymmetricCipher asymmetricCipher = AsymmetricCipher.create(AsymmetricAlgorithm.RSA);

        System.err.println("===========publicKey===========");
        System.err.println(asymmetricCipher.getBase64PublicKey());

        System.err.println("===========privateKey===========");
        System.err.println(asymmetricCipher.getBase64PrivateKey());

        byte[] sign = asymmetricCipher.sign(data.getBytes());
        System.err.println("sign:" + Base64.getEncoder().encodeToString(sign));

        boolean verify = asymmetricCipher.verify(data.getBytes(), sign);
        System.err.println("verify:" + verify);

        Assert.assertTrue(verify);
        System.err.println();
    }

    @Test
    public void testAsymmetricSign2() {
        String data = "非对称签名测试2";
        System.err.println(data);

        String publicKey = "MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGABmpaSji+l7CBZ9a3wtEHMWwpCUnZy66wGAff5fZuAHpPRB64ljSn0OAcBwhHBcjVBKvP+XUDExkACvAm//oz0bpbPzUeslw2yIELJDRr0oXFZCbJpPZWUBw9v4CiiWUBMoDMggjXQ8MNcLrZkOXK8q/jEJbLXrApFQb1AOmLf4g=";
        String privateKey = "MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUDzw/Nh7Wa4Wg4NnquitP0NgQ1+0=";
        AsymmetricCipher asymmetricCipher = AsymmetricCipher.create(AsymmetricAlgorithm.DSA, publicKey, privateKey);

        byte[] sign = asymmetricCipher.sign(data.getBytes());
        System.err.println("sign:" + Base64.getEncoder().encodeToString(sign));

        boolean verify = asymmetricCipher.verify(data.getBytes(), sign);
        System.err.println("verify:" + verify);

        Assert.assertTrue(verify);
        System.err.println();
    }


    @Test
    public void testSymmetricEncrypt1() {
        String data = "对称加密测试1";
        System.err.println(data);

        SymmetricCipher symmetricCipher = SymmetricCipher.create(SymmetricAlgorithm.DES);
        System.err.println("secretKey:" + symmetricCipher.getBase64SecretKey());

        byte[] encrypted = symmetricCipher.encrypt(data.getBytes());
        System.err.println("encrypted:" + Base64.getEncoder().encodeToString(encrypted));

        String decrypted = new String(symmetricCipher.decrypt(encrypted));
        System.err.println("decrypted:" + decrypted);

        Assert.assertEquals(data, decrypted);
        System.err.println();
    }

    @Test
    public void testSymmetricEncrypt2() {
        String data = "对称加密测试2";
        System.err.println(data);

        String secretKey = "6V2SlGS25bw=";
        SymmetricCipher symmetricCipher = SymmetricCipher.create(SymmetricAlgorithm.DES, secretKey);

        byte[] encrypted = symmetricCipher.encrypt(data.getBytes());
        System.err.println("encrypted:" + Base64.getEncoder().encodeToString(encrypted));

        String decrypted = new String(symmetricCipher.decrypt(encrypted));
        System.err.println("decrypted:" + decrypted);

        Assert.assertEquals(data, decrypted);
        System.err.println();
    }
}
