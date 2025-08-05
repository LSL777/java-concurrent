package com.linprogramming.java8.test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.TwoPhaseTermination")
public class TwoPhaseTermination {

    public static void main(String[] args) throws InterruptedException {
        Task task = new Task();
        task.start();
        Thread.sleep(2500);
        log.debug("发送停止请求");
        task.stop();
    }

    static class Task {

        /**
         * 任务线程
         */
        private Thread taskThread;

        /**
         * 启动任务
         */
        public void start() {
            taskThread = new Thread(() -> {
                while (true) {
                    boolean interrupted = Thread.currentThread().isInterrupted();
                    if (interrupted) {
                        log.debug("线程被中断");
                        break;
                    }
                    try {
                        // 睡眠期间被打断会清空中断状态
                        Thread.sleep(1000);
                        // 运行期间被打断不会清空中断状态
                        log.debug("执行任务");
                    } catch (InterruptedException e) {
                        // 睡眠期间收到中断信号，进入准备阶段（不立即退出），此时中断状态为false
                        log.debug("收到中断请求，准备停止");
                        // 恢复中断状态 设置为true
                        Thread.currentThread().interrupt();
                    }
                }
                cleanUp();
            });
            taskThread.start();
        }

        private void cleanUp() {
            log.debug("释放资源（关闭连接、保存数据等）");
        }

        /**
         * 停止任务
         */
        public void stop() {
            taskThread.interrupt();
        }
    }
}

