package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.TestBiased")
public class TestBiased {

    public static void main(String[] args) throws InterruptedException {
        // 测试偏向锁延迟开启
//        Dog dog = new Dog();
//        log.debug(ClassLayout.parseInstance(dog).toPrintable());
//
//        Thread.sleep(4000);
//        Dog dog1 = new Dog();
//        log.debug(ClassLayout.parseInstance(dog1).toPrintable());

        // 测试偏向锁 -XX:BiasedLockingStartupDelay=0
//        Dog d = new Dog();
//        ClassLayout classLayout = ClassLayout.parseInstance(d);
//        new Thread(() -> {
//            log.debug("synchronized 前");
//            System.out.println(classLayout.toPrintable());
//            synchronized (d) {
//                log.debug("synchronized 中");
//                System.out.println(classLayout.toPrintable());
//            }
//            log.debug("synchronized 后");
//            System.out.println(classLayout.toPrintable());
//        }, "t1").start();
        // 测试偏向锁与哈希码互斥 -XX:BiasedLockingStartupDelay=0
        Dog dog = new Dog();
        // 调用哈希码后会禁用掉偏向锁
        dog.hashCode();
        log.debug("打印哈希码后");
        log.debug(ClassLayout.parseInstance(dog).toPrintable());
        synchronized (dog) {
            log.debug("打印哈希码后进入同步代码块");
            log.debug(ClassLayout.parseInstance(dog).toPrintable());
        }
        log.debug("打印哈希码后退出同步代码块");
        log.debug(ClassLayout.parseInstance(dog).toPrintable());
    }
}

class Dog {}
