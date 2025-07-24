package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.CallableTest")
public class CallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("running");
                Thread.sleep(2000);
                return 0;
            }
        });
        Thread t1 = new Thread(futureTask, "t1");
        t1.start();
        log.debug("{}", futureTask.get());
    }
}
