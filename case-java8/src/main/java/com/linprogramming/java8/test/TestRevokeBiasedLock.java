package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.io.IOException;

/**
 * 测试撤销偏向锁
 *
 * @author LinShanglei
 */
@Slf4j(topic = "c.TestRevokeBiasedLock")
public class TestRevokeBiasedLock {


    public static void main(String[] args) {
//        biasedUpgradeLightWeight();
//        biasedRevokeWaitNotify();
        testRevoke();
    }

    /**
     * 撤销偏向锁
     * -XX:BiasedLockingStartupDelay=0运行
     */
    private static void testRevoke() {
        Dog d = new Dog();
        Thread t1 = new Thread(() -> {
            // 0x0000000000000001 (non-biasable; age: 0) 001：无锁状态
            log.debug(ClassLayout.parseInstance(d).toPrintable());
            synchronized (d) {
                // 0x0000003400bff530 0011010000000000101111111111010100110000 轻量级锁
                log.debug(ClassLayout.parseInstance(d).toPrintable());
            }
            // 0x0000000000000001 无锁状态
            log.debug(ClassLayout.parseInstance(d).toPrintable());
        }, "t1");
        t1.start();
    }

    /**
     * 调用wait/notify撤销偏向锁
     * -XX:BiasedLockingStartupDelay=0运行
     */
    private static void biasedRevokeWaitNotify() {
        Dog d = new Dog();
        Thread t1 = new Thread(() -> {
            // 0x0000000000000005 偏向锁
            log.debug(ClassLayout.parseInstance(d).toPrintable());
            synchronized (d) {
                // 0x000002b5119cd805 00101011010100010001100111001101100000000101 偏向锁
                log.debug(ClassLayout.parseInstance(d).toPrintable());
                try {
                    d.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 0x000002b57e320b4a 00101011010101111110001100100000101101001010 重量级锁
                log.debug(ClassLayout.parseInstance(d).toPrintable());
            }
        }, "t1");
        t1.start();
        new Thread(() -> {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (d) {
                log.debug("notify");
                d.notify();
            }
        }, "t2").start();
    }

    /**
     * 偏向锁升级为轻量级锁
     * -XX:BiasedLockingStartupDelay=0运行
     */
    private static void biasedUpgradeLightWeight() {
        Dog d = new Dog();
        Thread t1 = new Thread(() -> {
            synchronized (d) {
                // 0x000001dd7a670005 0001 11011101 01111010 01100111 00000000 00000101 偏向锁
                log.debug(ClassLayout.parseInstance(d).toPrintable());
            }
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            // 等待t1执行完
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //     0x000001dd7a670005 00000001 11011101 01111010 01100111 00000000 00000101 偏向锁
            log.debug(ClassLayout.parseInstance(d).toPrintable());
            synchronized (d) {
                // 0x0000001e117ff418 00000000 00011110 00010001 01111111 11110100 00011000 升级为轻量级锁
                log.debug(ClassLayout.parseInstance(d).toPrintable());
            }
            // 0x0000000000000001
            log.debug(ClassLayout.parseInstance(d).toPrintable());
        }, "t2");
        t2.start();
    }
}
