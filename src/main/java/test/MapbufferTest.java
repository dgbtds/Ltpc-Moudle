package test;

import Util.Convert;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Random;

/**
 * @author WuYe
 * @vesion 1.0 2019/12/9
 * /
 * /**
 * @program: Ltpc-Moudle
 * @description:
 * @author: WuYe
 * @create: 2019-12-09 13:43
 **/
public class MapbufferTest {
    private static final int Threshould=100;
    private static final int Rounds=2;

    static public void main( String args[] ) throws Exception {
        File file = new File("MappedByteBufferTest.txt");
        if(file.isFile()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file , "rw" );
        FileChannel fc = raf.getChannel();
        System.out.println("file size= "+raf.length());
        Random random = new Random();
        long position=0;
        long size=(Threshould*10)/Rounds;
        long num=Threshould/Rounds;
        for(int j=0;j<Rounds;j++){
            long finalPosition = position;
            MappedByteBuffer map = fc.map(FileChannel.MapMode.READ_WRITE, finalPosition, size);
            for (int i=0;i<num;i++) {
              //  int i1 = random.nextInt(Threshould) + 1;
                String s = String.format("%08d", j) + "\r\n";
                map.put(s.getBytes());
            }
            position+=size;
        map.force();
        }
        raf.close();
        System.out.println("size ="+file.length());
    }
}
