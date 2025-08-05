package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.InterruptSleepThread")
public class InterruptSleepThread {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("enter sleep");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.debug("wake up");
                e.printStackTrace();
            }
        }, "t1");
        t1.start();
        Thread.sleep(1000);
        log.debug("InterruptSleepThread");
        t1.interrupt();
    }
}
