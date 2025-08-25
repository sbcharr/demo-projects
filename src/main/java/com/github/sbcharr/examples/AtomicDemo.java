package com.github.sbcharr.examples;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicDemo {
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.incrementAndGet(); // Atomic + visible across threads
            }
        };

        Thread t1 = new Thread(runnable);
        Thread t2 = new Thread(runnable);

        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println("Final count = " + counter.get()); // Always 2000
    }
}

