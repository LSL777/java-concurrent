package com.linprogramming.java8.n4;


import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

/**
 * 批量重偏向/撤销
 *
 * @author LinShanglei
 */
@Slf4j(topic = "c.BatchBiasedOrRevoke")
public class BatchBiasedOrRevoke {

    static Thread t1, t2, t3;


//    |--------------------------------------------------------------------|--------------------|
//    |                Mark Word (64 bits)                                 | State              |
//    |--------------------------------------------------------------------|--------------------|
//    | unused:25 | hashcode:31 | unused:1 | age:4 | biased_lock:0   | 01  | Normal             |
//    |--------------------------------------------------------------------|--------------------|
//    | thread:54 | epoch:2     | unused:1 | age:4 | biased_lock:1   | 01  | Biased             |
//    |--------------------------------------------------------------------|--------------------|
//    |                ptr_to_lock_record:62                         | 00  | Lightweight Locked |
//    |--------------------------------------------------------------------|--------------------|
//    |                ptr_to_heavyweight_monitor:62                 | 10  | Heavyweight Locked |
//    |--------------------------------------------------------------------|--------------------|
//    |                                          .                   | 11  | Heavyweight Locked |
//    |--------------------------------------------------------------------|--------------------|


    public static void main(String[] args) throws InterruptedException {
//        testRebiased();
        testRevoke();
    }

    /**
     * 批量撤销
     * 以-XX:BiasedLockingStartupDelay=0运行
     * t1线程循环了39次，给39个对象都加了偏向锁，偏向t1线程
     * [t1] - 38 0x0000014948eea005  00000001 01001001 01001000 11101110 10100000 00000101
     * 从t1到t2，不能再偏向了，改为升级轻量级锁，解锁后变成不可偏向状态
     * [t2] - 0  0x0000014948eea005  00000001 01001001 01001000 11101110 10100000 00000101
     * [t2] - 0  0x0000002524fff138  00000000 00100101 00100100 11111111 11110001 00111000 这里加锁后已经变成轻量级锁状态了
     * [t2] - 0  0x0000000000000001  00000000 00000000 00000000 00000000 00000000 00000001 这里后三位是001，变成了无锁状态 mark word也不在保存线程id
     * 从t2-19开始就执行了批量重偏向的优化，
     * [t2] - 19 0x0000014948eea005  00000001 01001001 01001000 11101110 10100000 00000101
     * 重新偏向了t2
     * [t2] - 19 0x0000014948eeb105  00000001 01001001 01001000 11101110 10110001 00000101
     * [t2] - 19 0x0000014948eeb105  00000001 01001001 01001000 11101110 10110001 00000101
     * 一直到[t2] - 38 0x0000014948eeb105 都是偏向t2
     * t3开始加的都是轻量级锁，不可偏向，一直到 t3-18
     * [t3] - 0 0x0000000000000001 加锁前的mark word是001 不可偏向
     * [t3] - 0 0x00000025250ff368 加锁后的mark word是000 轻量级锁
     * [t3] - 0 0x0000000000000001 解锁后的mark word是001 不可偏向
     * 到19时
     * [t3] - 19 0x0000014948eeb105 00000001 01001001 01001000 11101110 10110001 00000101 加锁前偏向 t2线程
     * [t3] - 19 0x00000025250ff368 00000000 00100101 00100101 00001111 11110011 01101000 加锁后的mark word是000 轻量级锁
     * [t3] - 19 0x0000000000000001 解锁后的mark word是001 不可偏向，这里不会再次触发 批量重偏向 的优化，
     * 一直到38
     * [t3] - 38 0x0000014948eeb105
     * [t3] - 38 0x00000025250ff368
     * [t3] - 38 0x0000000000000001
     * 到第四十次时 [main] 0x0000000000000001 整个类的所有对象都会变为不可偏向的，新建的对象也是不可偏向
     */
    private static void testRevoke() throws InterruptedException {
        Vector<Dog> list = new Vector<>();
        int loopNumber = 39;
        t1 = new Thread(() -> {
            for (int i = 0; i < loopNumber; i++) {
                Dog d = new Dog();
                list.add(d);
                synchronized (d) {
                    log.debug("{}\t{}", i, ClassLayout.parseInstance(d).toPrintable());
                }
            }
            LockSupport.unpark(t2);
        }, "t1");
        t1.start();
        t2 = new Thread(() -> {
            LockSupport.park();
            log.debug("===============> ");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug("{}\t{}", i, ClassLayout.parseInstance(d).toPrintable());
                synchronized (d) {
                    log.debug("{}\t{}", i, ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug("{}\t{}", i, ClassLayout.parseInstance(d).toPrintable());
            }
            LockSupport.unpark(t3);
        }, "t2");
        t2.start();
        t3 = new Thread(() -> {
            LockSupport.park();
            log.debug("===============> ");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                log.debug("{}\t{}", i, ClassLayout.parseInstance(d).toPrintable());
                synchronized (d) {
                    log.debug("{}\t{}", i, ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug("{}\t{}", i, ClassLayout.parseInstance(d).toPrintable());
            }
        }, "t3");
        t3.start();
        t3.join();
        // 到这里已经撤销了39次了
        log.debug(ClassLayout.parseInstance(new Dog()).toPrintable());
    }

    /**
     * 批量重偏向
     * 以-XX:BiasedLockingStartupDelay=0运行
     */
    private static void testRebiased() {
        Vector<Dog> list = new Vector<>();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                Dog d = new Dog();
                list.add(d);
                synchronized (d) {
                    log.debug("{}\t{}", i, ClassLayout.parseInstance(d).toPrintable());
                }
            }
            synchronized (list) {
                list.notify();
            }
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (list) {
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("===============> ");
            for (int i = 0; i < 30; i++) {
                Dog d = list.get(i);
                log.debug("{}\t{}", i, ClassLayout.parseInstance(d).toPrintable());
                synchronized (d) {
                    // 在t1时偏向的线程ID与t2-19的线程ID一致，在t2-19加锁后触发阈值，直接重偏向到t2的线程
                    // [t1] - 19 0x000001d4e8202805  00000001 11010100 11101000 00100000 00101000 00000101
                    // [t2] - 19 0x000001d4e8202805  00000001 11010100 11101000 00100000 00101000 00000101
                    // [t2] - 19 0x000001d4e7f0f905  00000001 11010100 11100111 11110000 11111001 00000101
                    // [t2] - 19 0x000001d4e7f0f905  00000001 11010100 11100111 11110000 11111001 00000101
                    log.debug("{}\t{}", i, ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug("{}\t{}", i, ClassLayout.parseInstance(d).toPrintable());
            }
        }, "t2");
        t2.start();
    }
}

class Dog {
}
// # total entries: 0
//# biased lock entries: 85466
//# anonymously biased lock entries: 5942
//# rebiased lock entries: 699
//# revoked lock entries: 0
//# fast path lock entries: 29690
//# slow path lock entries: 167