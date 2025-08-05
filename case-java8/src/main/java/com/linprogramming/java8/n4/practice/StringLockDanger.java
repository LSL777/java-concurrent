package com.linprogramming.java8.n4.practice;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.StringLockDanger")
public class StringLockDanger {

    public static void main(String[] args) {
        new Thread(new TaskA()).start();
        new Thread(new TaskB()).start();
    }

    static class TaskA implements Runnable {
        @Override
        public void run() {
            synchronized ("LOCK") {
                log.debug("TaskA acquired lock, sleeping 5 seconds...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("TaskA 释放锁");
            }
        }
    }

    static class TaskB implements Runnable {
        @Override
        public void run() {
            try {
                // 确保TaskA先获得锁
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized ("LOCK") {
                log.debug("TaskB 获得锁");
            }
        }
    }
}
