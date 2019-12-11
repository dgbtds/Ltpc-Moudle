package test.fileCut;

import test.forkjoinPoolTest.ForkJoinCalculator;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * @author WuYe
 * @vesion 1.0 2019/12/10
 * /
 * /**
 * @program: Ltpc-Moudle
 * @description:
 * @author: WuYe
 * @create: 2019-12-10 16:35
 **/
public class DivideAconquerPool {
    private ForkJoinPool pool;
    private FileChannel channel=null;
    private int CountNum=0;
    private int THRESHOLD = 1000000;
    public  ForkJoinPool getPool(){
        return  pool;
    }
    //执行任务RecursiveTask：有返回值  RecursiveAction：无返回值
    private  class CTask extends RecursiveTask<Long> {
        private int start;
        private int end;
        public CTask() {

        }

        //此方法为ForkJoin的核心方法：对任务进行拆分  拆分的好坏决定了效率的高低
        @Override
        protected Long compute() {
            // 把任务一分为二，递归拆分(注意此处有递归)到底拆分成多少分 需要根据具体情况而定
            if (true){
                try {
                    MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, start, end);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

    }

    public DivideAconquerPool(FileChannel channel,int CountNum,int THRESHOLD) {
        // 也可以使用公用的线程池 ForkJoinPool.commonPool()：默认的线程数量:CPU的核数
        // pool = ForkJoinPool.commonPool()
        pool = new ForkJoinPool();
        this.channel=channel;
        this.CountNum=CountNum;
        this.THRESHOLD=THRESHOLD;
    }
}
