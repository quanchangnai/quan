using System;
using System.Text;
using NUnit.Framework;
using Quan.Cipher;
using Quan.Cipher;

namespace Test
{
    [TestFixture]
    public class CipherTest
    {
        [Test]
        public void TestSymmetricEncrypt1()
        {
            Console.WriteLine("TestSymmetric1");

            var cipher = new SymmetricCipher(SymmetricAlgorithm.Des);

            Console.WriteLine($"secretKey:{cipher.Base64SecretKey}");

            var encrypted = cipher.Encrypt(Encoding.UTF8.GetBytes("对称加密测试1"));
            Console.WriteLine($"encrypted:{Convert.ToBase64String(encrypted)}");

            var decrypted = cipher.Decrypt(encrypted);
            Console.WriteLine($"decrypted:{Encoding.UTF8.GetString(decrypted)}");
        }

        [Test]
        public void TestSymmetricEncrypt2()
        {
            Console.WriteLine("TestSymmetric2");

            var secretKey = Convert.FromBase64String("6V2SlGS25bw=");
            var cipher = new SymmetricCipher(SymmetricAlgorithm.Des, secretKey);

            var encrypted = cipher.Encrypt(Encoding.UTF8.GetBytes("对称加密测试2"));
            Console.WriteLine($"encrypted:{Convert.ToBase64String(encrypted)}");

            // encrypted = Convert.FromBase64String("uMi0zTne1HMv5sU1HB2WI/CsNvBUn5y3");

            var decrypted = cipher.Decrypt(encrypted);
            Console.WriteLine($"decrypted:{Encoding.UTF8.GetString(decrypted)}");
        }

        [Test]
        public void TestAsymmetricEncrypt1()
        {
            var data = "非对称加密测试1";
            Console.WriteLine(data);

            var cipher = new AsymmetricCipher(AsymmetricAlgorithm.Rsa);

            Console.WriteLine("=====================publicKey================");
            Console.WriteLine(cipher.Base64PublicKey);
            Console.WriteLine();

            Console.WriteLine("=====================privateKey================");
            Console.WriteLine(cipher.Base64PrivateKey);
            Console.WriteLine();

            var encrypted = cipher.Encrypt(Encoding.UTF8.GetBytes(data));
            Console.WriteLine($"encrypted:{Convert.ToBase64String(encrypted)}");

            var decrypted = cipher.Decrypt(encrypted);
            Console.WriteLine($"decrypted:{Encoding.UTF8.GetString(decrypted)}");
        }

        [Test]
        public void TestAsymmetricEncrypt2()
        {
            var data = "非对称加密测试2";
            Console.WriteLine(data);

            var publicKey =
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjoHfRKSPSURhRYJu9UtgkvP2TFjOI4fHbXUmsBgIU1TiNugP/JepY5lMZ6ISF3zg0QO5DTJ09god76BZVFKnKAsWA4Dqcv3bNVUneSZygtsB/SCzjUQ9o8bZkiCd5vaAOER/Z6g75jtl8XGtjE7GtQ/ezd37JQR7xZ2axZrWtdQIDAQAB";
            var privateKey =
                "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKOgd9EpI9JRGFFgm71S2CS8/ZMWM4jh8dtdSawGAhTVOI26A/8l6ljmUxnohIXfODRA7kNMnT2Ch3voFlUUqcoCxYDgOpy/ds1VSd5JnKC2wH9ILONRD2jxtmSIJ3m9oA4RH9nqDvmO2Xxca2MTsa1D97N3fslBHvFnZrFmta11AgMBAAECgYBGaYVmApghpzgZvMMII6BTnuhX5VPj8acMSQas+iDnKiIeCxAxOfWwr9zO51ov6bDb+50MZOm9UHBRB7ykfDHbxkFXCLA3Tesr2/X3+0Iyz30pu1ctCOcjlJaHQpnE09okYI9kZpkTzzK3Hkm59wE/9SOor5Ag70hrXcAApwIceQJBANKXscBScD6nySdmbxBjfPc+UwSEFIwb+XYey3hJHNY9rKuA4opRTTWMB9JaFfgeXeRP8y5Fd+B8Y1R6n9z5KKcCQQDG6FsBtEmj6mVL/ZPr2JeUAo1Z7VH9uB6bG5o2sTeeERAz3GSqZd+K7s/LSjbJ9XbIuGHoFrE4wT+uIfwD9yCDAkAyUVKEXG47WkXC50PES7ExNjAJ1TE/pPN/GK6PKBD+06+tLtdyKyjikXnQ9ftn1IGkqsG1HZ4eAjqNldsapmHjAkApMEZgJPg21Dvjr3/pD7HbuWeR3p3i3zSfQ+j8OFhfCAOF6baCvpO6zlcDLrwHuCe/ysaja8eJDCNmqKzqGUuHAkBlTjExgc35J2TY1pn4FE1pqj8Yavll4vfiDhIrRwLdVJyKq8Sjm4OzVFAkk3rMkqPR/ZL3cxb45RMmtGkYwVQ9";

            var cipher = new AsymmetricCipher(AsymmetricAlgorithm.Rsa, publicKey, privateKey);

            var encrypted = cipher.Encrypt(Encoding.UTF8.GetBytes("非对称加密测试2"));
            Console.WriteLine($"encrypted:{Convert.ToBase64String(encrypted)}");

            // encrypted = Convert.FromBase64String("js3wD7eeMtEVyZvO0HrvlRU5esVrmXOyW21c+woVC5r9no1xm3KT1IBMcrGPWj8YL1NIjdBW+Qfq81bCRC1Oqsxou/5IZOIV4JFSnY0k1yxF+8TH9IWXsfU/nrFyQagQrJZ3gGG7NygIM6dxK8StsCLTVNmJX8vxvwPNqITql1A=");

            var decrypted = cipher.Decrypt(encrypted);
            Console.WriteLine($"decrypted:{Encoding.UTF8.GetString(decrypted)}");
        }

        [Test]
        public void TestAsymmetricSign1()
        {
            var text = "非对称签名测试1";
            Console.WriteLine(text);

            var cipher = new AsymmetricCipher(AsymmetricAlgorithm.Rsa);
            Console.WriteLine("=====================publicKey================");
            Console.WriteLine(cipher.Base64PublicKey);
            Console.WriteLine();

            Console.WriteLine("=====================privateKey================");
            Console.WriteLine(cipher.Base64PrivateKey);
            Console.WriteLine();

            var data = Encoding.UTF8.GetBytes(text);
            var sign = cipher.Sign(data);
            Console.WriteLine($"sign:{Convert.ToBase64String(sign)}");

            var verify = cipher.Verify(data, sign);
            Console.WriteLine($"verify:{verify}");
        }


        [Test]
        public void TestAsymmetricSign2()
        {
            var text = "非对称签名测试2";
            Console.WriteLine(text);

            var publicKey =
                "MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGABmpaSji+l7CBZ9a3wtEHMWwpCUnZy66wGAff5fZuAHpPRB64ljSn0OAcBwhHBcjVBKvP+XUDExkACvAm//oz0bpbPzUeslw2yIELJDRr0oXFZCbJpPZWUBw9v4CiiWUBMoDMggjXQ8MNcLrZkOXK8q/jEJbLXrApFQb1AOmLf4g=";
            var privateKey =
                "MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUDzw/Nh7Wa4Wg4NnquitP0NgQ1+0=";

            var cipher = new AsymmetricCipher(AsymmetricAlgorithm.Dsa, publicKey, privateKey);

            var data = Encoding.UTF8.GetBytes(text);
            var signature = cipher.Sign(data);
            Console.WriteLine($"signature:{Convert.ToBase64String(signature)}");

            signature = Convert.FromBase64String("MCwCFGSjitt2rP7A8p3mcFmjjkuvAsCUAhQvlzprmCWVqEbLrs928Kl2QA2h2g==");

            var verification = cipher.Verify(data, signature);
            Console.WriteLine($"verification:{verification}");
        }
    }
}