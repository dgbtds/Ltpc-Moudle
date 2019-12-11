package test.forkjoinPoolTest;

/**
 * @author WuYe
 * @vesion 1.0 2019/12/9
 * /
 * /**
 * @program: Ltpc-Moudle
 * @description:
 * @author: WuYe
 * @create: 2019-12-09 15:49
 **/
public interface Calculator {
    /**
     * 把传进来的所有numbers 做求和处理
     *
     * @param numbers
     * @return 总和
     */
    long sumUp(long[] numbers);
}
