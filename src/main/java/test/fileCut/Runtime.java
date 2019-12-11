package test.fileCut;

import test.forkjoinPoolTest.ForkJoinCalculator;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * @author WuYe
 * @vesion 1.0 2019/12/10
 * /
 * /**
 * @program: Ltpc-Moudle
 * @description:
 * @author: WuYe
 * @create: 2019-12-10 15:47
 **/
public interface Runtime {
    default void RunT() throws Exception {
        Instant start = Instant.now();
        this.run();
        Instant end = Instant.now();
        System.out.println("\n"+"**** process useTime:" + Duration.between(start, end).toMillis() + "ms");
    };
    void run() throws Exception;

}
