package com.linprogramming.java8.n4;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.GuardedObjectSimple")
public class GuardedObjectSimple {

    public static void main(String[] args) {
        GuardedObject guardedObject = new GuardedObject();
        new Thread(() -> {
            log.debug("等待结果");
            Object response = null;
//            try {
//                response = guardedObject.get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            // 超时版本
            response = guardedObject.get(3000);
            log.debug("结果：{}", response);
        }, "t1").start();

        new Thread(() -> {
            log.debug("开始处理任务");
//            guardedObject.done("任务处理结果");
            // 超时版本
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            guardedObject.done("任务处理结果");
        }, "t2").start();
    }
}

@Slf4j(topic = "c.GuardedObject")
class GuardedObject {

    /**
     * 封装结果
     */
    private Object response;

    private final Object lock = new Object();

    public Object get(long timeout) {
        synchronized (lock) {
            long currentTimeMillis = System.currentTimeMillis();
            while (Objects.isNull(response)) {
                long waitTime = System.currentTimeMillis() - currentTimeMillis;
                if (waitTime >= timeout) {
                    log.debug("等待超时");
                    break;
                }
                try {
                    lock.wait(timeout - waitTime);
                } catch (InterruptedException e) {
                    log.error("wait error: ", e);
                    Thread.currentThread().interrupt();
                }
                log.debug("timePassed: {}, object is null {}", waitTime, response == null);
            }
            return response;
        }
    }

    public Object get() throws InterruptedException {
        synchronized (lock) {
            while (Objects.isNull(response)) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    log.error("wait error: ", e);
                    // 这里捕获异常后必须 恢复中断状态 或者 向上传播
                    // 如果只捕获异常并打日志，后续代码（包括上层调用者）会以为 “从未发生过中断”，导致：
                    // 1. 线程可能继续执行不需要的任务（比如用户已经取消的操作），浪费资源。
                    // 2. 上层代码无法感知 “取消信号”，无法做出正确的终止处理。
                    // 以代码最下面的注释示例
                    throw e;
                }
            }
            return response;
        }
    }

    public void done(Object response) {
        synchronized (lock) {
            this.response = response;
            lock.notifyAll();
        }
    }
}

// downloadBlock()只负责 “下载一块数据”，它不知道 “被中断后是否要终止整个下载”（可能只是临时中断，稍后继续）。
// 把异常抛给上层downloadFile()，由它根据业务场景决定：比如用户取消下载时，就清理资源并终止。
/// 你的方法：负责下载一块数据
/// private byte[] downloadBlock() throws InterruptedException {  // 向上传播异常
///     synchronized (networkLock) {
///         while (没有数据) {
///             networkLock.wait();  // 等待时可能被中断
///         }
///         return 数据;
///     }
/// }
///
/// 上层方法：控制整个下载流程
/// public void downloadFile() {
///     try {
///         while (还有数据要下载) {
///             byte[] block = downloadBlock();  // 调用你的方法
///             保存数据到文件;
///         }
///     } catch (InterruptedException e) {
///         // 上层知道被中断了，做清理工作
///         删除不完整的文件;
///         通知用户“下载已取消”;
///         return;  // 终止整个下载
///     }
/// }


