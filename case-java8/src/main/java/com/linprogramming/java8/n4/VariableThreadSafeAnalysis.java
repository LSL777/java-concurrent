package com.linprogramming.java8.n4;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.VariableThreadSafe")
public class VariableThreadSafeAnalysis {

    static final int THREAD_NUMBER = 2;

    static final int LOOP_NUMBER = 100;

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            test1();
        }, "t1");
        Thread t2 = new Thread(() -> {
            test1();
        }, "t2");
        t1.start();
        t2.start();

        // 线程不安全 所有的线程都是共享同一个实例的成员变量，且对该成员变量有修改的操作
//        MemberVariableThreadSafe memberVariableThreadSafe = new MemberVariableThreadSafe();
//        for (int i = 0; i < THREAD_NUMBER; i++) {
//            new Thread(() -> {
//                for (int j = 0; j < LOOP_NUMBER; j++) {
//                    memberVariableThreadSafe.test2();
//                }
//            }).start();
//        }
        // 等待循环结束后再调用MemberVariableThread的print打印list的大小
//        while (Thread.activeCount() > 1) {
//            Thread.yield();
//        }
//        memberVariableThreadSafe.print();


        LocalVariableThreadSubClassUnsafe memberLocalVariableThreadSubClassUnsafe = new LocalVariableThreadSubClassUnsafe();
        for (int i = 0; i < THREAD_NUMBER; i++) {
            new Thread(() -> {
                for (int j = 0; j < LOOP_NUMBER; j++) {
                    memberLocalVariableThreadSubClassUnsafe.test3();
                }
            }).start();
        }
    }

    /**
     * 线程安全 每一个线程调用该方法后都会有一个自己私有的栈帧，栈帧中保存着该方法中的局部变量，并未共享
     */
    public static void test1() {
        int i = 10;
        i++;
        log.debug("{}", i);
    }
}

@Slf4j(topic = "c.MemberVariableThread")
class MemberVariableThreadSafe {
    /**
     * 线程不安全
     */
    ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1));

    public void print() {
        log.debug("{}", list.size());
    }

    public void test2() {
        add();
        remove();
    }

    private void add() {
        list.add(5);
    }

    private void remove() {
        list.remove(0);
    }


    public void test3() {
        ArrayList<Integer> list = new ArrayList<>();
        // 线程不安全，虽然是局部变量，父类的pubAdd和pubRemove使用的都是同一个list，但是因为两个方法
        // 都是公有的，有可能会被子类覆盖方法重写，所以线程不安全
        for (int i = 0; i < 100; i++) {
            pubAdd(list);
            pubRemove(list);
        }
    }

    public void pubAdd(ArrayList<Integer> list) {
        list.add(5);
    }

    public void pubRemove(ArrayList<Integer> list) {
        list.remove(0);
    }
}


class LocalVariableThreadSubClassUnsafe extends MemberVariableThreadSafe {
    @Override
    public void pubAdd(ArrayList<Integer> list) {
        // 子类重新开了线程修改了list，其他线程也修改了list，此时的list是共享资源
        new Thread(() -> {
            list.add(5);
        }).start();
    }
}
