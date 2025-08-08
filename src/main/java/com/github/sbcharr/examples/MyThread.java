package com.github.sbcharr.examples;

import java.util.List;
import java.util.stream.Collectors;

// Thread creation by extending Thread class
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread by extending the Thread class");
    }
}

// Thread creation by implementing Runnable interface
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Thread by implementing the Runnable interface");
    }
}

class MainApp {
    public static void main(String[] args) {
        // Thread via extending Thread class
        MyThread thread1 = new MyThread();
        thread1.start();

        // Thread via implementing Runnable interface
        Thread thread2 = new Thread(new MyRunnable());
        thread2.start();

        List<Integer> l1 = List.of(1, 2, 3, 4, 5, 6);
        List<Integer> l2 = l1.stream()
                .filter(x -> x % 2 == 0)
                .map(x -> x * x)
                .collect(Collectors.toList());
    }
}

