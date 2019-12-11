package test.forkjoinPoolTest;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * @author WuYe
 * @vesion 1.0 2019/12/9
 * /
 * /**
 * @program: Ltpc-Moudle
 * @description:
 * @author: WuYe
 * @create: 2019-12-09 16:22
 **/
public class ForkJoinCalculator implements Calculator {
    private ForkJoinPool pool;
    private final static int CountNum= 100000000;
    public static void main(String[] args) {
        long[] numbers = LongStream.rangeClosed(1, CountNum).toArray();
        Instant start = Instant.now();
        ForkJoinCalculator calculator = new ForkJoinCalculator();
        long result = calculator.sumUp(numbers);
        Instant end = Instant.now();
        System.out.println("useTime:" + Duration.between(start, end).toMillis() + "ms");
        System.out.println("result:" + result);
        System.out.println("#########################################################"+"\n");
        System.out.println("getParallelism"+calculator.pool.getParallelism()+"\n");
    }
    public int getActiveThreadCount(){
        return pool.getActiveThreadCount();
    }
    //执行任务RecursiveTask：有返回值  RecursiveAction：无返回值
    private static class SumTask extends RecursiveTask<Long> {
        private final static int THRESHOLD = 1000000;
        private final static int CountNum0 = CountNum;
        private long[] numbers;
        private int from;
        private int to;
       static  {
            System.out.println("forkJoin thread num : "+CountNum0/THRESHOLD);
        }

        public SumTask(long[] numbers, int from, int to) {
            this.numbers = numbers;
            this.from = from;
            this.to = to;
        }

        //此方法为ForkJoin的核心方法：对任务进行拆分  拆分的好坏决定了效率的高低
        @Override
        protected Long compute() {
                 // 把任务一分为二，递归拆分(注意此处有递归)到底拆分成多少分 需要根据具体情况而定
                if (to-from<THRESHOLD){
                    Long sum=0L;
                    for(int i=from;i<=to;i++){
                       sum+=numbers[i];
                    }
                    return sum;
                }
                int middle = (from + to) / 2;
                SumTask taskLeft = new SumTask(numbers, from, middle);
                SumTask taskRight = new SumTask(numbers, middle + 1, to);
                taskLeft.fork();
                taskRight.fork();
                return taskLeft.join() + taskRight.join();
            }

    }

    public ForkJoinCalculator() {
        // 也可以使用公用的线程池 ForkJoinPool.commonPool()：默认的线程数量:CPU的核数
        // pool = ForkJoinPool.commonPool()
        pool = new ForkJoinPool();
    }

    @Override
    public long sumUp(long[] numbers) {
        Long result = pool.invoke(new SumTask(numbers, 0, numbers.length-1 ));
        pool.shutdown();
        return result;
    }
}
