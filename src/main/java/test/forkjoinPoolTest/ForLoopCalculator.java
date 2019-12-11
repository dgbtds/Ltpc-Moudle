package test.forkjoinPoolTest;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.LongStream;

/**
 * @author WuYe
 * @vesion 1.0 2019/12/9
 * /
 * /**
 * @program: Ltpc-Moudle
 * @description:
 * @author: WuYe
 * @create: 2019-12-09 15:48
 **/
public class ForLoopCalculator implements Calculator{
    public static void main(String[] args) {
        long[] numbers = LongStream.rangeClosed(1, 100000000).toArray();

        Instant start = Instant.now();
        Calculator calculator = new ForLoopCalculator();

        long result = calculator.sumUp(numbers);
        Instant end = Instant.now();
        System.out.println("useTime:" + Duration.between(start, end).toMillis() + "ms");
        System.out.println("result:" + result);
    }
    @Override
    public long sumUp(long[] numbers) {
        long total = 0;
        for (long i : numbers) {
            total += i;
        }
        return total;
    }
}
