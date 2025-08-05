package com.linprogramming.java8.test;


import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.StopDemo2")
public class StopDemo2 {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                log.debug("线程开始执行");
                try {
                    Thread.sleep(1000); // 模拟工作
                } finally {
                    log.debug("finally 块：释放资源");
                }
            } catch (InterruptedException ignored) {}
        });

        t.start();
        Thread.sleep(10); // 稍等一下
        t.stop(); // 强制终止

        // 没有输出 "finally 块：释放资源"
    }
}
