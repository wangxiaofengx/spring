package com.mrwang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ForkJoinTest extends RecursiveTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int start;
    private int end;
    private List<Integer> datas;
    private int threshold = 10;

    private static volatile int count = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger();

    public ForkJoinTest(List<Integer> datas, int start, int end) {
        this.datas = datas;
        this.start = start;
        this.end = end;
    }

    public ForkJoinTest() {
    }


    public void test() throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        List<Integer> list = new ArrayList<>();
        int length = 100;
        for (int i = 0; i < length; i++) {
            list.add((int) (Math.random() * length));
        }
        System.out.println(Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
        ForkJoinTest forkJoinTest = new ForkJoinTest(list, 0, length - 1);
        Future f = forkJoinPool.submit(forkJoinTest);
        System.out.println("max:" + f.get() + " active:" + Thread.activeCount());
        System.out.println(count + "\t" + atomicInteger.get() + "\t" + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
    }


    @Override
    protected Object compute() {
        count++;
        System.out.println(count + "\t" + atomicInteger.getAndIncrement() + "\t" + Thread.currentThread().getId() + ":" + Thread.currentThread().getName());
        if (this.end - this.start < threshold) {
            int max = 0;
            for (int i = start; i <= end; i++) {
                max = Math.max(this.datas.get(i), max);
            }
            return max;
        }
        int middle = this.start + (this.end - this.start) / 2;
        ForkJoinTest left = new ForkJoinTest(this.datas, this.start, middle);
        ForkJoinTest right = new ForkJoinTest(this.datas, middle + 1, this.end);
        left.fork();
        right.fork();
        invokeAll(left, right);
        return Math.max((int) left.join(), (int) right.join());
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinTest forkJoinTest = new ForkJoinTest();
        System.out.println((int) (Math.random() * 1000000));
        forkJoinTest.test();
        System.out.println(atomicInteger.get());
        System.out.println(atomicInteger.incrementAndGet());
        System.out.println(atomicInteger.incrementAndGet());
        System.out.println(atomicInteger.get());

    }
}
