package com;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ThreadTest {

    Map<String, Object> info = new HashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        ThreadTest threadTest = new ThreadTest();
        for (int i = 0; i < 10; i++) {
            Object result = threadTest.run(i + "");
        }
    }

    Object run(String id) throws ExecutionException, InterruptedException, TimeoutException {
        FutureTask futureTask = new FutureTask(() -> request(id));
        Thread thread = new Thread(futureTask);
        thread.start();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            receive(id);
        }).start();
        return futureTask.get(6L, TimeUnit.SECONDS);
    }

    String request(String id) {
        info.put(id, id);
        synchronized (id) {
            try {
                System.out.println(Thread.currentThread().getId() + "\t发送请求：" + id);
                id.wait();
                String result = info.get(id).toString();
                System.out.println(Thread.currentThread().getId() + "\t返回结果：" + result);
                return result;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    void receive(String id) {
        Object value = info.get(id);
        if (value != null) {
            synchronized (value) {
                info.put(id, id + ":" + UUID.randomUUID().toString());
                value.notify();
            }
        }
    }
}
