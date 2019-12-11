package test.fileCut;

import java.util.concurrent.*;

/**
 * @author WuYe
 * @vesion 1.0 2019/12/11
 * /
 * /**
 * @program: Ltpc-Moudle
 * @description:
 * @author: WuYe
 * @create: 2019-12-11 16:03
 **/
public class TestExcutors {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(java.lang.Runtime.getRuntime().availableProcessors()
                , new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread("Read-File");
                    }
                });
        class findMaxTask implements Runnable{
            private int[]arr;
            private int[] maxNum;
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()+"***Actival thread num***"+((ThreadPoolExecutor)executorService).getActiveCount());
                // 最后一个任务开始

            }
        }
        for(int i=0;i<10;i++){
            findMaxTask findMaxTask = new findMaxTask();
            executorService.submit(findMaxTask);
        }
        executorService.shutdown();
        while (!executorService.awaitTermination(10,TimeUnit.MILLISECONDS)){

        }
    }
}
