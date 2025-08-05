package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

class User {
    String name;
    int age;

    public synchronized void set(String name, int age) {
        this.name = name;
        try {
            // 模拟延迟
            Thread.sleep(100); 
        } catch (InterruptedException ignored) {
            ignored.printStackTrace();
        }
        this.age = age;
    }

    public synchronized String toString() {
        return name + " - " + age;
    }
}

@Slf4j(topic = "c.StopDemo1")
public class StopDemo1 {
    public static void main(String[] args) throws InterruptedException {
        User user = new User();

        Thread t1 = new Thread(() -> user.set("Alice", 25));
        t1.start();

        // 让线程进行一半
        Thread.sleep(50);
        // 强制终止
        t1.stop();

        // 可能输出：Alice - 0（状态不一致）
        log.debug("user = " + user);
    }
}
