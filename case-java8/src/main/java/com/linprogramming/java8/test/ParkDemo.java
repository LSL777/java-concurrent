package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.ParkDemo")
public class ParkDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("park...");
            // 阻塞
            LockSupport.park();
            log.debug("unpark...");
//            log.debug("打断标记: {}", Thread.currentThread().isInterrupted());
            log.debug("打断标记: {}", Thread.interrupted());

            // 如果打断标记为true，则park不会生效，如果需要生效可以执行 Thread.interrupted();重置为false
            LockSupport.park();
            log.debug("unpark...");
        }, "t1");
        t1.start();
        Thread.sleep(1000);
        t1.interrupt();
    }
}
