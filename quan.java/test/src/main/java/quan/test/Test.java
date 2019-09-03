package quan.test;

import quan.message.Buffer;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by quanchangnai on 2019/7/10.
 */
public class Test {

    public static void main(String[] args) throws Exception {

        Buffer buffer = new Buffer();
        buffer.writeInt(70);
        buffer.writeInt(2423);
        buffer.writeFloat(13.43F);
        buffer.writeDouble(4242.432);
        buffer.writeFloat(132.32434F, 2);
        buffer.writeDouble(342254.653254, 2);
        buffer.writeString("搭顺风车");
        buffer.reset();

        System.err.println("buffer.available()=" + buffer.available());

        FileInputStream fileInputStream = new FileInputStream(new File("E:\\buffer"));
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes);
        System.err.println("bytes.length=" + bytes.length);

        buffer = new Buffer(bytes);
        System.err.println(buffer.readInt());
        System.err.println(buffer.readInt());
        System.err.println(buffer.readFloat());
        System.err.println(buffer.readDouble());
        System.err.println(buffer.readFloat(2));
        System.err.println(buffer.readDouble(2));
        System.err.println(buffer.readString());

//        buffer.reset();
//        buffer.writeInt(45);
//        buffer.writeString("奋斗服务");
//        System.err.println(buffer.readInt());
//        System.err.println(buffer.readString());
    }

}
