package edu.nwpu.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainHandler implements Runnable {
    //12845辆Bus
    private static final int NUMBER = 2;
    private static List<BusHandler> busHandlers = new ArrayList<BusHandler>();
    private static ExecutorService exec = Executors.newCachedThreadPool();

    private double latitude = 114.533;
    private double longitude = 22.1234;

    public void run() {

        for (BusHandler bushandler : busHandlers) {
            System.out.println(bushandler.getPaths());
        }
/*
        int result[] = BusHandler.result;
        System.out.println("飞<" + latitude + "," + longitude + ">最短的t1+t2:" +
                result[0] + "\nrowNumber1:" + result[1] +
                "\nrowNumber2:" + result[2]);
*/

        exec.shutdown();

        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            System.out.println("barrier-action sleep interrupted");
        }
    }

    @Test
    public void mainTest() {
        CyclicBarrier barrier = new CyclicBarrier(NUMBER, new MainHandler());
        for (int i = 0; i < NUMBER; i++) {
            BusHandler busHandler = new BusHandler(barrier, latitude, longitude);
            busHandlers.add(busHandler);
            exec.execute(busHandler);
        }
    }
}
