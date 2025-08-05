package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.StopDemo3")
public class StopDemo3 {
    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    log.debug("线程1获取了锁");
                    Thread.sleep(5000); // 模拟长时间处理
                } catch (InterruptedException ignored) {}
            }
        });

        t1.start();
        Thread.sleep(500); // 确保t1获取了锁

        t1.stop(); // 强制终止t1
        log.debug("线程1已被停止");

        // 尝试让另一个线程获取锁
        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                log.debug("线程2成功获取锁");
            }
        });

        t2.start();
    }
}
