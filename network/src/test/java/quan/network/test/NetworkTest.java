package quan.network.test;

/**
 * Created by quanchangnai on 2017/7/3.
 */
public class NetworkTest {
    public static void main(String[] args) {
        printInt(0b10000000);
        printInt(0b11111111);
        printInt(0b1111111);
        printInt(0x7F);

    }

    private static void printInt(int i) {
        System.err.println(i+":"+Integer.toBinaryString(i)+":"+Integer.toHexString(i));
    }

    private static void printLong(long i) {
        System.err.println(i+":"+Long.toBinaryString(i)+":"+Long.toHexString(i));
    }
}
