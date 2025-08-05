package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.InterruptWaitExample")
public class InterruptWaitExample {
    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        Thread t = new Thread(() -> {
            log.debug(" status: {}", Thread.currentThread().isInterrupted());
            synchronized (lock) {
                try {
                    log.debug("线程进入wait()");
                    lock.wait(); // 线程在此处被阻塞
                } catch (InterruptedException e) {
                    log.debug("中断状态: " + Thread.currentThread().isInterrupted());
                    e.printStackTrace();
                }
            }
        });
        t.start();
        Thread.sleep(1000);
//        t.interrupt();
    }
}