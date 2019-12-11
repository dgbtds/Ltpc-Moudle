package test;

/**
 * @author WuYe
 * @vesion 1.0 2019/12/9
 * /
 * /**
 * @program: Ltpc-Moudle
 * @description:
 * @author: WuYe
 * @create: 2019-12-09 15:42
 **/
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:ForkJoinPoolAction <br/>
 * Function: 使用ForkJoinPool完成一个任务的分段执行
 * 简单的打印0-300的数值。用多线程实现并行执行
 * Date:     2017年12月4日 下午2:26:55 <br/>
 * @author   prd-lxw
 * @version   1.0
 * @since    JDK 1.7
 * @see
 */
public class ForkJoinPoolAction {

    public static void main(String[] args) throws Exception{
        PrintTask task = new PrintTask(0, 300);
        //创建实例，并执行分割任务
        ForkJoinPool pool = new ForkJoinPool();
        pool.submit(task);
        //线程阻塞，等待所有任务完成
        pool.awaitTermination(2, TimeUnit.SECONDS);
        pool.shutdown();
    }
}

/**
 * ClassName: PrintTask <br/>
 * Function: 继承RecursiveAction来实现“可分解”的任务。
 * date: 2017年12月4日 下午5:17:41 <br/>
 *
 * @author prd-lxw
 * @version 1.0
 * @since JDK 1.7
 */
class PrintTask extends RecursiveAction{
    private static final int THRESHOLD = 50; //最多只能打印50个数
    private int start;
    private int end;

    public PrintTask(int start, int end) {
        super();
        this.start = start;
        this.end = end;
    }



    @Override
    protected void compute() {

        if(end - start < THRESHOLD){
            for(int i=start;i<end;i++){
                System.out.println(Thread.currentThread().getName()+"的i值："+i);
            }
        }else {
            int middle =(start+end)/2;
            PrintTask left = new PrintTask(start, middle);
            PrintTask right = new PrintTask(middle, end);
            //并行执行两个“小任务”
            left.fork();
            right.fork();
        }

    }

}
