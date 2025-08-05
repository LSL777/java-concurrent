package com.linprogramming.java8.n4;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.SyncDemo1")
public class SyncDemo1 {
    static int counter = 0;
    static final Object room = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (room) {
                for (int i = 0; i < 5000; i++) {
                    counter++;
                }
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            synchronized (room) {
                for (int i = 0; i < 5000; i++) {
                    counter--;
                }
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.debug("{}", counter);
    }
}
