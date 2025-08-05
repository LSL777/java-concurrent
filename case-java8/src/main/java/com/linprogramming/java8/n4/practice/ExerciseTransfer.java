package com.linprogramming.java8.n4.practice;

import lombok.extern.slf4j.Slf4j;
import sun.misc.GC;

import java.util.Random;


/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.ExerciseTransfer")
public class ExerciseTransfer {

    protected static final Object LOCK = new Object();

    public static void main(String[] args) throws InterruptedException {
        Account a = new Account(1000);
        Account b = new Account(1000);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                a.transfer(b, randomAmount());
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                b.transfer(a, randomAmount());
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // 查看转账2000次后的总金额
        log.debug("total:{}", (a.getMoney() + b.getMoney()));
    }

    // Random 为线程安全
    static Random random = new Random();

    // 随机 1~100
    public static int randomAmount() {
        return random.nextInt(100) + 1;
    }
}

class Account {
    private int money;

    public Account(int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }


//    public void transfer(Account target, int amount) {
//        // 线程安全，但是不建议这样做
//        synchronized (Account.class) {
//            if (this.money > amount) {
//                this.setMoney(this.getMoney() - amount);
//                target.setMoney(target.getMoney() + amount);
//            }
//        }
//    }

    public void transfer(Account target, int amount) {
        if (this.money > amount) {
            this.setMoney(this.getMoney() - amount);
            target.setMoney(target.getMoney() + amount);
        }
    }

    // 线程不安全，这样只是给实例对象加锁，只能影响一个账户的余额
//    public synchronized void transfer(Account target, int amount) {
//        if (this.money > amount) {
//            this.setMoney(this.getMoney() - amount);
//            target.setMoney(target.getMoney() + amount);
//        }
//    }

//    public void transfer(Account target, int amount) {
//        synchronized (LOCK) {
//            if (this.money > amount) {
//                this.setMoney(this.getMoney() - amount);
//                target.setMoney(target.getMoney() + amount);
//            }
//        }
//    }
}