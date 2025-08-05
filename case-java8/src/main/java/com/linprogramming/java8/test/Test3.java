package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.Test3")
public class Test3 {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            int i = 0;
            while (true) {
                i++;
                boolean interrupted = Thread.currentThread().isInterrupted();
                if (interrupted) {
                    log.debug("线程被中断");
                    break;
                }
            }
            log.debug("结束, i = {}", i);
        }, "t1");
        t1.start();
        Thread.sleep(1000);
        log.debug("Interrupted");
        t1.interrupt();
    }
}
