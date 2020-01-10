package com.mrwang.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class ForkJoinTest extends RecursiveTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int start;
    private int end;
    private List<Integer> datas;
    private int threshold = 10;

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
        int length = 30;
        for (int i = 0; i < length; i++) {
            list.add((int) (Math.random() * length));
        }

        ForkJoinTest forkJoinTest = new ForkJoinTest(list, 0, length-1);
        Future f = forkJoinPool.submit(forkJoinTest);
        System.out.println(f.get());
    }


    @Override
    protected Object compute() {

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
        invokeAll(left,right);
        return Math.max((int) left.join(), (int) right.join());
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinTest forkJoinTest = new ForkJoinTest();
//        System.out.println((int) (Math.random() * 1000000));
        forkJoinTest.test();
    }
}
