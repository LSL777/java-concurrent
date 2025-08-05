package com.linprogramming.java8.n4;

/**
 * @author LinShanglei
 */
public class SynchronizedAnalysis {

    static final Object LOCK = new Object();

    static int count = 0;

    public static void main(String[] args) {
        synchronized (LOCK) {
            count++;
        }
    }
}


