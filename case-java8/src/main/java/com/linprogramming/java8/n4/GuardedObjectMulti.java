package com.linprogramming.java8.n4;

import lombok.extern.slf4j.Slf4j;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LinShanglei
 */
@Slf4j(topic = "c.GuardedObjectMMulti")
public class GuardedObjectMulti {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            new People().start();
        }
        Thread.sleep(1500);
        for (Integer id : Mailboxes.getIds()) {
            new Postman(id, "内容" + id).start();
        }
    }
}

@Slf4j(topic = "c.People")
class People extends Thread {
    @Override
    public void run() {
        // 收信
        GuardedObjectM guardedObject = Mailboxes.createGuardedObjectM();
        log.debug("开始收信 id:{}", guardedObject.getId());
        Object mail = guardedObject.get(5000);
        log.debug("收到信 id:{}, 内容:{}", guardedObject.getId(), mail);
    }
}

@Slf4j(topic = "c.Postman")
class Postman extends Thread {
    private int id;

    private String mail;

    public Postman(int id, String mail) {
        this.id = id;
        this.mail = mail;
    }

    @Override
    public void run() {
        GuardedObjectM guardedObject = Mailboxes.getGuardedObjectM(id);
        log.debug("送信 id:{}, 内容:{}", id, mail);
        guardedObject.complete(mail);
    }
}


class GuardedObjectM {

    private int id;

    public GuardedObjectM(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private Object response;


    public Object get(long timeout) {
        synchronized (this) {
            // 开始时间 15:00:00
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passedTime = 0;
            while (response == null) {
                // 这一轮循环应该等待的时间
                long waitTime = timeout - passedTime;
                // 经历的时间超过了最大等待时间时，退出循环
                if (timeout - passedTime <= 0) {
                    break;
                }
                try {
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 求得经历时间
                passedTime = System.currentTimeMillis() - begin;
            }
            return response;
        }
    }


    public void complete(Object response) {
        synchronized (this) {
            // 给结果成员变量赋值
            this.response = response;
            this.notifyAll();
        }
    }
}

class Mailboxes {

    private static Map<Integer, GuardedObjectM> boxes = new ConcurrentHashMap<>(16);

    private static int id = 1;

    private static synchronized int generateId() {
        return id++;
    }

    public static GuardedObjectM getGuardedObjectM(int id) {
        // GuardedObjectM是一次性的对象，使用完毕就删除
        return boxes.remove(id);
    }

    public static GuardedObjectM createGuardedObjectM() {
        GuardedObjectM go = new GuardedObjectM(generateId());
        boxes.put(go.getId(), go);
        return go;
    }

    public static Set<Integer> getIds() {
        return boxes.keySet();
    }
}