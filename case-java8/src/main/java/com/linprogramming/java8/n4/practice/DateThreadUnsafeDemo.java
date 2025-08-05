package com.linprogramming.java8.n4.practice;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * 模拟Servlet类
 */
@Slf4j(topic = "c.MyServlet")
class MyServlet {
    /**
     * 共享的Date实例（模拟Servlet中的成员变量）
     */
    private final Date sharedDate = new Date();
    
    /**
     * 处理请求的方法（模拟Servlet的service方法）
     * 如果出现以下情况，则证明线程不安全：
     *   1. 同一时间点，不同线程打印的时间值不一致。
     *   2. 时间值不符合预期（如线程 1 设置后，线程 2 读到的时间不是线程 1 设置的）。
     * @param threadId 线程ID
     */
    public void processRequest(int threadId) {
        try {
            // 多个线程同时读取和修改sharedDate
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // 线程1设置时间为当前时间
            if (threadId == 1) {
                sharedDate.setTime(System.currentTimeMillis());
                log.debug("线程: {} 设置时间为: {}", threadId, sdf.format(sharedDate));
            } 
            // 线程2设置时间为10秒前
            else if (threadId == 2) {
                sharedDate.setTime(System.currentTimeMillis() - 10000);
                log.debug("线程: {} 设置时间为: {}", threadId, sdf.format(sharedDate));
            }
            
            // 模拟处理延迟
            Thread.sleep(100);
            
            // 所有线程读取sharedDate的值
            String formattedDate = sdf.format(sharedDate);
            log.debug("线程" + threadId + "读取时间: " + formattedDate);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/**
 * @author LinShanglei
 */
public class DateThreadUnsafeDemo {
    public static void main(String[] args) throws InterruptedException {
        // 创建Servlet实例
        MyServlet servlet = new MyServlet();
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(10); 
        
        // 模拟10个并发请求
        for (int i = 1; i <= 10; i++) {
            // 交替使用线程1和线程2的逻辑
            final int threadId = i % 2 + 1; 
            executor.submit(() -> servlet.processRequest(threadId));
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);
    }
}