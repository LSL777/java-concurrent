package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.ThreadStateDemo")
public class ThreadStateDemo {
    public static void main(String[] args) throws IOException {
        // new
        Thread t1 = new Thread(() -> {
            log.debug("running...");
        }, "t1");


        // runnable
        Thread t2 = new Thread(() -> {
            while (true) {

            }
        }, "t2");
        t2.start();

        // terminated
        Thread t3 = new Thread(() -> {
            log.debug("running...");
        }, "t3");
        t3.start();

        // time_waiting
        Thread t4 = new Thread(() -> {
            synchronized (ThreadStateDemo.class) {
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t4");
        t4.start();

        // waiting
        Thread t5 = new Thread(() -> {
            try {
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t5");
        t5.start();

        // blocked
        Thread t6 = new Thread(() -> {
            synchronized (ThreadStateDemo.class) {
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t6");
        t6.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("t1 state {}", t1.getState());
        log.debug("t2 state {}", t2.getState());
        log.debug("t3 state {}", t3.getState());
        log.debug("t4 state {}", t4.getState());
        log.debug("t5 state {}", t5.getState());
        log.debug("t6 state {}", t6.getState());
        System.in.read();
    }
}
