package com.github.sbcharr.examples;

public class VolatileDemo {
    private static volatile boolean running = true; // ensures visibility

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            while (running) {
                // busy working
            }
            System.out.println("Worker stopped");
        });

        worker.start();
        Thread.sleep(1000);   // let the worker run
        running = false;      // stop signal is visible to the worker
        worker.join();
    }
}


