package edu.nwpu.test;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;

class Horse implements Runnable {

    private static int counter = 0;
    private final int id = counter++;
    private int strides = 0;
    private static Random rand = new Random(47);
    private static CyclicBarrier barrier;

    public Horse(CyclicBarrier b) { barrier = b; }

    public void run() {
        try {
            while(!Thread.interrupted()) {
/*                synchronized(this) {
                    //赛马每次随机跑几步
                    strides += rand.nextInt(3);
                }*/
                strides += rand.nextInt(3);
                barrier.await();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String tracks() {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < getStrides(); i++) {
            s.append("*");
        }
        s.append(id);
        return s.toString();
    }

    public synchronized int getStrides() { return strides; }
    public String toString() { return "Horse " + id + " "; }

}