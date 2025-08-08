package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.WaitNotifyDemo")
public class WaitNotifyDemo {

    static final Object obj = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (obj) {
                log.debug("t1 start...");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("t1 end...");
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (obj) {
                log.debug("t2 start...");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("t2 end...");
        }, "t2");
        t2.start();
        Thread.sleep(2000);
        synchronized (obj) {
            log.debug("main start...");
            obj.notify();
//            obj.notifyAll();
        }
    }
}
