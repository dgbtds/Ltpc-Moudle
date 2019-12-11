package test.fileCut;


import javax.swing.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author WuYe
 * @vesion 1.0 2019/12/10
 * /
 * /**
 * @program: Ltpc-Moudle
 * @description:
 * @author: WuYe
 * @create: 2019-12-10 14:16
 **/
public class MapReduceWork implements Runtime{
    private static final int Threshould=100000000;
    private static final int MaxNum=100;
    private static final int Rounds=10;
    private static final int ReadTaskNum=10;
    private static final int dataSize=10;
    private  static int readFileThreadNum=6;
    private static final int RoundCount= (int) Math.sqrt((double) Threshould*MaxNum);
    //最终数据出口
    private int[] MaxAll=new int[MaxNum];
    private AtomicInteger Position = new AtomicInteger(0);
    public static void main(String[] args) throws Exception {
        MapReduceWork mapReduceWork = new MapReduceWork();
            mapReduceWork.RunT();
    }
    @Override
    public void run() throws IOException, InterruptedException {
        // findMax0("testP.txt",MaxNum);
         //findMaxBySPMC("testP.txt",MaxNum);
            findMaxByMPMC("testP.txt",MaxNum);
    }
    public void findMax0(String path,int MaxNum) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));
        int[] maxNum = new int[MaxNum];
        String s="";int i=0;
        while ((s!=null)){
            s = bufferedReader.readLine();
            if (s==null){
                System.out.println("Count of Num: "+i);
                break;
            }
            Integer integer = Integer.valueOf(s);
            insert(maxNum,integer);
            i++;
        }
        System.out.println("maxNum:");
        for (int a:maxNum){
            System.out.print(a+" - ");
        }
    }
    public void findMaxByMPMC(String file,int MaxNum) throws IOException, InterruptedException {
        int[] last=new int[RoundCount];
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileChannel channel = randomAccessFile.getChannel();
        int findMaxThreadNum = java.lang.Runtime.getRuntime().availableProcessors()-readFileThreadNum;
        ExecutorService executorReadFileService = Executors.newFixedThreadPool(readFileThreadNum);
        ExecutorService executorFindMaxService = Executors.newFixedThreadPool(findMaxThreadNum);
        HashMap<String, Long> readFileMap = new HashMap<>();
        for(int K=1;K<=readFileThreadNum;K++){
            String threadName="pool-1-thread-"+K;
            readFileMap.put(threadName,0L);
        }
        HashMap<String, Long> findMaxMap = new HashMap<>();
        for(int K=1;K<=findMaxThreadNum;K++){
            String threadName="pool-2-thread-"+K;
            findMaxMap.put(threadName,0L);
        }
        long position=0;
        long size=(Threshould*dataSize)/ReadTaskNum;
        long num=Threshould/ReadTaskNum;
        for(int i=0;i<ReadTaskNum;i++){
            ReadTask readTask = new ReadTask(channel, size, position, executorFindMaxService,last,readFileMap,findMaxMap);
            executorReadFileService.submit(readTask);
            position+=size;
        }
        executorReadFileService.shutdown();
        while (Position.get()<last.length){

        }
        System.out.println("last task start");
        findMaxTask findMaxTask = new findMaxTask(last,MaxAll,last,findMaxMap);
        executorFindMaxService.submit(findMaxTask);
        executorFindMaxService.shutdown();
        while (!executorReadFileService.awaitTermination(10, TimeUnit.MILLISECONDS)) {
        }
        while (!executorFindMaxService.awaitTermination(10, TimeUnit.MILLISECONDS)) {
        }
        System.out.print("MaxAll:");
        for (int a:MaxAll){
            System.out.print(a+" - ");
        }
        System.out.println("\n%%%%%%%%%% PRODUCER:CONSUMER%%%%%%%%%%="+readFileThreadNum+":"+findMaxThreadNum+"\n");
        long sum=0;
        for (Map.Entry<String ,Long> entry:readFileMap.entrySet()){
            System.out.println("readFileMap:"+entry.getKey()+" Occupy time :"+entry.getValue()+"ms");
            sum+=entry.getValue();
        }
        System.out.println("readFileMap:sum of Occupy time: "+sum+"ms\n");
        sum=0;
        for (Map.Entry<String ,Long> entry:findMaxMap.entrySet()){
            System.out.println("findMaxMap:"+entry.getKey()+" Occupy time :"+entry.getValue()+"ms");
            sum+=entry.getValue();
        }
        System.out.println("findMaxMap:sum of Occupy time: "+sum+"ms");

    }
    class ReadTask implements Runnable{
        private FileChannel channel;
        private long size;
        private long finalPosition;
        private  ExecutorService executorFindMaxService ;
        private  int[] last ;
        private HashMap readFileMap;
        private HashMap findMaxMap;

        public ReadTask(FileChannel channel, long size, long finalPosition, ExecutorService executorFindMaxService, int[] last, HashMap readFileMap, HashMap findMaxMap) {
            this.channel = channel;
            this.size = size;
            this.finalPosition = finalPosition;
            this.executorFindMaxService = executorFindMaxService;
            this.last = last;
            this.readFileMap = readFileMap;
            this.findMaxMap = findMaxMap;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            try {
                MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, finalPosition, size);
                int[] ints=new int[RoundCount];int rounds=0;
                byte[] numbers=new byte[dataSize];
                int remain=map.remaining();
                int i=0;
                while (remain>=dataSize) {
                    map.get(numbers);
                    if ((numbers[8]!=0x0d)||(numbers[9]!=0x0a)){
                        long errorPosition=finalPosition+size-remain;
                        throw new RuntimeException("raw error line"+errorPosition/dataSize);
                    }
                    String s = new String(numbers, 0, 8, Charset.defaultCharset());
                    Integer integer = Integer.valueOf(s);
                    ints[i]=integer;
                    remain=map.remaining();
                    i++;
                    if (i>=ints.length) {
                        findMaxTask findMaxTask = new findMaxTask(ints,new int[MaxNum],last,findMaxMap);
                        executorFindMaxService.submit(findMaxTask);
                        i=0;
                        int[] ints0=new int[RoundCount];
                        ints=ints0;
                        rounds++;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            Long aLong = (Long) readFileMap.get(Thread.currentThread().getName());
            readFileMap.put(Thread.currentThread().getName(),aLong+end-start);
        }
    }
    class findMaxTask implements Runnable{
        private int[]arr;
        private int[] maxNum;
        private int[] last;
        private HashMap hashMap;
        public findMaxTask(int[] arr, int[] maxNum, int[] last, HashMap hashMap) {
            this.arr = arr;
            this.maxNum = maxNum;
            this.last = last;
            this.hashMap = hashMap;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            for (int i = 0;i<arr.length;i++) {
                insert(maxNum,arr[i]);
            }
            if (Position.get()<RoundCount) {
                for(int i=0;i<MaxNum;i++){
                    last[Position.getAndIncrement()]=maxNum[i];
                }
            }
            long end = System.currentTimeMillis();
            Long aLong = (Long) hashMap.get(Thread.currentThread().getName());
            hashMap.put(Thread.currentThread().getName(),aLong+end-start);

        }
    }
    public void findMaxBySPMC(String path,int MaxNum) throws IOException, InterruptedException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));
        String s="";int i=0;
        int[] last=new int[RoundCount];
        ExecutorService executorService = Executors.newFixedThreadPool(java.lang.Runtime.getRuntime().availableProcessors());
        HashMap<String, Long> hashMap = new HashMap<>();
       for(int K=1;K<=12;K++){
           String threadName="pool-1-thread-"+K;
           hashMap.put(threadName,0L);
       }
        // ExecutorService executorService = Executors.newFixedThreadPool(1);

        int[] ints=new int[RoundCount];int rounds=0;
        // while ((s!=null)&&rounds<2){
        while ((s!=null)){
            s = bufferedReader.readLine();
            //最后一次可能不满数组
            if (s==null){
                System.out.println("file end ###"+" []Length : "+i);
                if (i!=0) {
                    findMaxTask findMaxTask = new findMaxTask(ints,new int[MaxNum],last,hashMap);
                    rounds++;
                    executorService.submit(findMaxTask);
                }
                break;
            }
            Integer integer = Integer.valueOf(s);
            ints[i]=integer;
            i++;
            if (i>=ints.length) {
                findMaxTask findMaxTask = new findMaxTask(ints,new int[MaxNum],last,hashMap);
                executorService.submit(findMaxTask);
                i=0;
                int[] ints0=new int[RoundCount];
                ints=ints0;
                rounds++;
            }
        }
        while (Position.get()<last.length){

        }
        System.out.println("last task start");
        findMaxTask findMaxTask = new findMaxTask(last,MaxAll,last,hashMap);
        executorService.submit(findMaxTask);
        executorService.shutdown();
        while (!executorService.awaitTermination(10, TimeUnit.MILLISECONDS)) {
        }

        System.out.println("MaxAll:");
        for (int a:MaxAll){
            System.out.print(a+" - ");
        }
        System.out.println("");
        long sum=0;
        for (Map.Entry<String ,Long> entry:hashMap.entrySet()){
            System.out.println(entry.getKey()+" Occupy time :"+entry.getValue()+"ms");
            sum+=entry.getValue();
        }
        System.out.println("sum of Occupy time: "+sum+"ms");
    }

    static void insert(int[]arr,int a){
        for(int i=0;i<arr.length;i++){
            if (a<arr[i]){
                break;
            }
            else {
                if(i==0){
                    arr[0]=a;
                }
                else {
                    arr[i-1]=arr[i];
                    arr[i]=a;
                }
            }
        }
    }


}
