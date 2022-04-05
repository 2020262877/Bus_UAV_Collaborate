package test;

import org.junit.Test;

import java.util.concurrent.*;

public class ThreadTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            public String call() throws Exception {
                return "12346";
            }
        });
        new Thread(task).start();
        System.out.println(task.get());
    }

    @Test
    public void test2() {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 1; i <= 10; i++) {
            final int index = i;
            try {
                Thread.sleep(index * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    String threadName = Thread.currentThread().getName();
                    System.out.println("执行：" + index + "，线程名称：" + threadName);
                }
            });
        }
    }

}
