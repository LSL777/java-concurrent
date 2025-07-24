package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.Test1")
public class Test1 {

    public static void main(String[] args) {
        // 匿名内部类
        Thread thread = new Thread(() -> {
            log.debug("test running");
        }, "test");
        thread.start();
//        thread.run();
        ThreadClass threadClass = new ThreadClass();
        threadClass.start();
        log.debug("main running");
    }
}

/**
 * 线程类
 */
@Slf4j(topic = "c.ThreadClass")
class ThreadClass extends Thread {
    @Override
    public void run() {
        log.debug("{}: ThreadClass running", Thread.currentThread().getName());
    }
}
