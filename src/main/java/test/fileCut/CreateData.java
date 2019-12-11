package test.fileCut;

import jdk.nashorn.internal.ir.IfNode;

import javax.jws.WebParam;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author WuYe
 * @vesion 1.0 2019/12/11
 * /
 * /**
 * @program: Ltpc-Moudle
 * @description:
 * @author: WuYe
 * @create: 2019-12-11 13:30
 **/
public class CreateData implements Runtime{
    private static final int Threshould=100000000;
    private static final int Rounds=10;

    public static void main(String[] args) throws Exception {
        new CreateData().RunT();
    }
    @Override
    public void run() throws Exception {
        //write("test.txt");
        writeP("testP.txt");
    }
    public static void write(String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file, false);
        int i=1;
        Random random = new Random();
        while (i<=Threshould){
            int i1 = random.nextInt(Threshould) ;
            String s = String.format("%08d", i1) + "\r\n";
            fileWriter.write(s);
            i++;
        }
        fileWriter.close();
        System.out.println("size ="+file.length());
    }
    public static void writeP(String fileName) throws IOException, InterruptedException {
        File file = new File(fileName);
        if(file.isFile()) {
            file.delete();
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileChannel channel = randomAccessFile.getChannel();
        ExecutorService executorService = Executors.newFixedThreadPool(java.lang.Runtime.getRuntime().availableProcessors());
        long position=0;
        long size=(Threshould*10)/Rounds;
        long num=Threshould/Rounds;
        for(int i=0;i<Rounds;i++){
            long finalPosition = position;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Random random = new Random();
                        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, finalPosition, size);
                        for (int i=0;i<num;i++) {
                            int i1 = random.nextInt(Threshould);
                            String s = String.format("%08d", i1) + "\r\n";
                            map.put(s.getBytes());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
            position+=size;
        }
            executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) {
        }
        channel.close();
        System.out.println("size ="+file.length());
    }
    public static void read() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("test.txt")));
        byte[] bytes = new byte[1024];
        String s="";
        int i=0;
        while ((s!=null)&&i<100){
            s = bufferedReader.readLine();
            System.out.println((i++)+" line:  "+s.length()+"--->"+s);
            System.out.println("##################################");
        }

    }


}
