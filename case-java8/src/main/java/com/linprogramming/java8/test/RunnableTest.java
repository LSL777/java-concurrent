package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

import java.security.AccessControlContext;
import java.security.AccessController;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.RunnableTest")
public class RunnableTest {

    public static void main(String[] args) {
        // 匿名内部类
        Runnable r = () -> log.debug("running...");
        Thread thread = new Thread(r, "RunnableThread");
        thread.start();

        // 创建线程类
        MyRunnable myRunnable = new MyRunnable();
        Thread thread1 = new Thread(myRunnable);
        thread1.start();
    }
}

@Slf4j(topic = "c.MyRunnable")
class MyRunnable implements Runnable {
    @Override
    public void run() {
        log.debug("{}: running...", Thread.currentThread().getName());
    }
}